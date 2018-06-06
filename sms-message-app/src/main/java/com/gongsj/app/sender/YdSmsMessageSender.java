package com.gongsj.app.sender;

import com.alibaba.fastjson.JSONObject;
import com.gongsj.core.SmsRemainingNumberResponse;
import com.gongsj.core.SmsSimpleSendResponse;
import com.gongsj.core.exception.PlatArrearsException;
import com.gongsj.core.property.SmsPlatformProperties;
import com.gongsj.core.sender.AbstractBasicSmsMessageSender;

import com.gongsj.core.sender.PersistentSaveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import sun.misc.BASE64Encoder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


@Slf4j
public class YdSmsMessageSender extends AbstractBasicSmsMessageSender {

    private final static BASE64Encoder BASE_64_ENCODER = new BASE64Encoder();

    private AtomicLong remaining;

    public YdSmsMessageSender(PersistentSaveRepository persistentSaveRepository, SmsPlatformProperties platformProperties) {
        super(persistentSaveRepository, platformProperties);
    }


    @Override
    public SmsRemainingNumberResponse getRemainingNumber() {
        if (remaining == null) {
            throw new IllegalStateException("移动平台的剩余短信数量的查询，需要联系客户，并且在系统中进行设置");
        }
        SmsRemainingNumberResponse numberResponse = new SmsRemainingNumberResponse();
        numberResponse.setSuccess(true);
        numberResponse.setDescription("移动平台的剩余短信数量的详细值，需要联系客户，系统自提供近似值");
        numberResponse.setNumber(remaining.get());

        return numberResponse;
    }


    @Override
    public SmsSimpleSendResponse sendMessage(String sysCode, String phoneNumber, String content, String templateId) throws PlatArrearsException {
        Assert.hasText(content, "content is required");
        Assert.state(remaining!=null, "The remaining amount has not been set");
        remaining.decrementAndGet();
        if (content.length() > 69) {
            remaining.decrementAndGet();
        }

        long remaining = this.remaining.get();
        log.info("yd platform SMS remaining:{}", remaining);
        if (remaining < 1) {
            throw new PlatArrearsException();
        }
        return super.sendMessage(sysCode, phoneNumber, content, templateId);
    }

    @Override
    protected Object generateSendSmsRequestBody(String phoneNumber, String content, String templateId) {
        Map<String, String> properties = platformProperties.getProperties();
        Map<String, String> requestBody = new HashMap<>(12);
        requestBody.put(platformProperties.getMobileProperty(), phoneNumber);
        requestBody.put(platformProperties.getContentProperty(), content);
        requestBody.putAll(properties);

        String macValue = properties.get("ecName") +
                properties.get("apId") +
                properties.get("secretKey") +
                phoneNumber +
                content +
                properties.get("sign") +
                properties.get("addSerial");
        requestBody.put("mac", DigestUtils.md5DigestAsHex(macValue.getBytes()));
        return BASE_64_ENCODER.encode(JSONObject.toJSONString(requestBody).getBytes());
    }


    public void setRemaining(long remaining) {
        Assert.isTrue(remaining != 0, "remaining =0");
        this.remaining = new AtomicLong(remaining);
        log.info("yd platform SMS remaining set to {}", remaining);
    }
}
