package com.gongsj.core.sender;

import com.alibaba.fastjson.JSONObject;


import com.gongsj.core.SmsRemainingNumberResponse;
import com.gongsj.core.SmsSimpleSendResponse;
import com.gongsj.core.annotation.MessageResponseProcessorType;
import com.gongsj.core.domain.MessageRecord;
import com.gongsj.core.domain.MessageUser;
import com.gongsj.core.exception.PlatArrearsException;
import com.gongsj.core.parse.JsonHttpResponseParser;
import com.gongsj.core.parse.ParamsHttpResponseParser;
import com.gongsj.core.parse.SmsHttpResponseParser;
import com.gongsj.core.property.SmsPlatformProperties;
import com.gongsj.core.property.SmsPlatformProperties.SendResponseProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;



import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.springframework.util.Assert.hasText;

/**
 * 只处理基本的发送信息的流程，根据所有拥有的{@link SmsPlatformProperties}进行相关的发送。
 *
 * @see org.springframework.web.client.RestTemplate <code>http</code>请求所使用的工具类
 * @see SmsPlatformProperties
 */
@Slf4j
public abstract class AbstractBasicSmsMessageSender implements SmsMessageSender {

    protected SmsHttpResponseParser[] httpResponseParsers;

    protected RestOperations restTemplate = new RestTemplate();

    protected final PersistentSaveRepository persistentSaveRepository;

    protected HttpHeaders[] httpHeaders;

    protected final SmsPlatformProperties platformProperties;

    public AbstractBasicSmsMessageSender(PersistentSaveRepository persistentSaveRepository, SmsPlatformProperties platformProperties) {
        this.persistentSaveRepository = persistentSaveRepository;
        this.platformProperties = platformProperties;

        //初始化解析器
        this.httpResponseParsers = new SmsHttpResponseParser[]{new ParamsHttpResponseParser(), new JsonHttpResponseParser()};
        //初始化header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_FORM_URLENCODED + ";charset=gbk"));
        this.httpHeaders = new HttpHeaders[]{new HttpHeaders(), headers};
    }


    @Override
    public SmsSimpleSendResponse sendMessage(String sysCode, String phoneNumber, String content, String templateId) throws PlatArrearsException {
        Assert.hasText(phoneNumber, "phoneNumber is required");
        Assert.hasText(content, "content is required");

        MessageRecord messageRecord = new MessageRecord();

        messageRecord.setPhoneNumber(Arrays.asList(phoneNumber.split(",")));

        messageRecord.setPlatformName(platformProperties.getPlatform());

        messageRecord.setContent(content);

        messageRecord.setTemplateId(templateId);

        long start = System.currentTimeMillis();

        SmsSimpleSendResponse simpleResponse = this.doSendMessage(phoneNumber, content, templateId);

        long end = System.currentTimeMillis();

        messageRecord.setResponseTime(end - start);

        messageRecord.setSuccess(simpleResponse.getSuccess());

        messageRecord.setMessage(simpleResponse.getMessage());

        //为其设置MessageUser
        messageRecord.setMessageUser(new MessageUser(sysCode));

        persistentSaveRepository.save(messageRecord);

        //每超过70算一条
        int count = ((int) Math.ceil((double) content.length() / 70d));
        //多个号码
        int phoneNumberCount = phoneNumber.split(",").length;
        //总共消耗
        simpleResponse.setConsume(phoneNumberCount * count);


        return simpleResponse;
    }

    /**
     * 通过<code>responseType</code>来获取对应的{@link SmsHttpResponseParser}，
     * 如果在Spring的容器中没有获取到，则需要通过{@link MessageResponseProcessorType}来指定。
     *
     * @param responseType 相应结果的类型，需要在配置文件中进行配置
     * @return
     * @see MessageResponseProcessorType
     */
    private SmsHttpResponseParser getResponseResultProcessor(String responseType) {
        if (StringUtils.isBlank(responseType)) {
            responseType = AnnotationUtils.findAnnotation(this.getClass(), MessageResponseProcessorType.class).value();
        }
        if ("json".equalsIgnoreCase(responseType)) {
            return httpResponseParsers[1];
        }
        if ("params".equalsIgnoreCase(responseType)) {
            return httpResponseParsers[0];
        }
        throw new IllegalStateException("unsupported parser responseType：" + responseType);
    }

    /**
     * 将<code>content</code>指定的内容发送给指定的<code>phoneNumber</code>
     *
     * @param phoneNumber 接受短信方的电话号码
     * @param content     发送的内容
     * @return 返回 {@link SmsSimpleSendResponse}，当发送消息失败时，记录原因。
     */
    protected SmsSimpleSendResponse doSendMessage(String phoneNumber, String content, String templateId) throws PlatArrearsException {


        try {
            //响应属性
            final SendResponseProperty response = platformProperties.getSendResponse();
            //请求地址
            final String apiAddress = platformProperties.getSendApiAddress();
            //请求体
            final Object requestBody = generateSendSmsRequestBody(phoneNumber, content, templateId);

            final HttpEntity<Object> httpEntity = new HttpEntity<>(requestBody, getRequestHeaders(platformProperties.getHttpHeaders()));


            String postResponse = restTemplate.postForObject(apiAddress, httpEntity, String.class);
            log.info("SMS sent operation has been completed, returns the result:{},platform:{}", postResponse, platformProperties.getPlatform());
            SmsSimpleSendResponse simpleResponse = getResponseResultProcessor(response.getType()).parse(postResponse, response);

            String message = simpleResponse.getMessage();
            if (StringUtils.isNotBlank(message)) {
                simpleResponse.setMessage(this.convertCharset(message));
            }

            return simpleResponse;
        } catch (RestClientException e) {
            log.error("REQUEST FAILED->{}", e.getMessage());
            return new SmsSimpleSendResponse(false, e.getMessage());
        }
    }

    private HttpHeaders getRequestHeaders(String httpHeadersType) {
        if ("application/x-www-form-urlencoded".equalsIgnoreCase(httpHeadersType)) {
            return httpHeaders[1];
        }
        return httpHeaders[0];
    }


    protected Object generateSendSmsRequestBody(String phoneNumber, String content, String templateId) {
        MultiValueMap<String, String> requestParams = generateBaseRequestBody();
        requestParams.add(platformProperties.getMobileProperty(), phoneNumber);
        requestParams.add(platformProperties.getContentProperty(), content);
        //当有templateId时，进行属性的添加
        if (StringUtils.isNotBlank(templateId)) {
            requestParams.add(platformProperties.getTemplateIdProperty(), templateId);
        }
        log.trace("The request parameters were successfully constructed-->{}", JSONObject.toJSONString(requestParams));
        return requestParams;
    }

    /**
     * @return 返回通过配置获得的请求体。
     */
    private MultiValueMap<String, String> generateBaseRequestBody() {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        platformProperties.getProperties().forEach(requestParams::add);
        return requestParams;
    }

    private String convertCharset(String sourceMessage) {
        hasText(sourceMessage, "sourceMessage is null or empty");
        SendResponseProperty response = platformProperties.getSendResponse();
        try {
            return new String(sourceMessage.getBytes(response.getAcceptCharset()), response.getTargetCharset());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("acceptCharset:{} or targetCharset:{} is unsupported charset,convert failed", response.getAcceptCharset(), response.getTargetCharset());
        }
        return sourceMessage;
    }

    @Override
    public SmsSimpleSendResponse sendMessage(String sysName, String phoneNumber, String content) throws PlatArrearsException {
        return sendMessage(sysName, phoneNumber, content, null);
    }

    @Override
    public String getPlatform() {
        return platformProperties.getPlatform();
    }


    @Override
    public SmsRemainingNumberResponse getRemainingNumber() {
        final String remainingNumberApiAddress = platformProperties.getRemainingNumberApiAddress();

        final String responseString = restTemplate.postForObject(remainingNumberApiAddress, new HttpEntity<Object>(generateBaseRequestBody(), getRequestHeaders(platformProperties.getHttpHeaders())), String.class);
        final String result = convertCharset(responseString);
        SmsHttpResponseParser responseResultProcessor = getResponseResultProcessor(platformProperties.getSendResponse().getType());
        return responseResultProcessor.parse(result, platformProperties.getRemainingNumberResponse());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractBasicSmsMessageSender)) {
            return false;
        } else {
            AbstractBasicSmsMessageSender otherBasicSmsMessageSender = (AbstractBasicSmsMessageSender) obj;
            return otherBasicSmsMessageSender.platformProperties.equals(this.platformProperties);
        }
    }
}
