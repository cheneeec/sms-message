package com.gongsj.app.sender;

import com.alibaba.fastjson.JSONObject;
import com.gongsj.app.AES;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;


public class SmsMessageSenderTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String mobile = "18280045913";
    private String content = "同事您好，感谢您对此次测试的配合。123456";
    private Map<String, String> params = new LinkedHashMap<>();

    /**
     * 梦网V
     */
    @Test
    public void mwSend() throws UnsupportedEncodingException {
        String url = "http://61.145.229.29:9002/sms/v2/std/single_send";
        String userId = "JC2407";
        String pwd = "364644";
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss"));
        params.put("userid", userId);
        params.put("pwd", DigestUtils.md5DigestAsHex((userId + "00000000" + pwd + timeStamp).getBytes()));
        params.put("mobile", mobile);
        params.put("content", URLEncoder.encode(content, "GBK"));
        params.put("timestamp", timeStamp);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> httpEntity = new HttpEntity<>(params, httpHeaders);
        System.out.println(restTemplate.postForEntity(url, httpEntity, String.class));
    }

    /**
     * 亿美软通
     * bjmtn.b2m.cn
     * shmtn.b2m.cn
     */
    @Test
    public void ymSend() throws UnsupportedEncodingException {
        // 接口地址
        String url = "http://shmtn.b2m.cn/simpleinter/sendPersonalitySMS";
        // appId
        String appId = "EUCP-EMY-SMS0-JKTQN";
        //密钥
        String secretkey = "9865725803995250";
        // 加密算法
        String algorithm = "AES/ECB/PKCS5Padding";
        String localDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        params.put("requestTime", localDateTime);
        params.put("mobile", mobile);
        params.put("content", content);
        params.put("requestValidPeriod", "30");
        params.put("sign", DigestUtils.md5DigestAsHex((appId + secretkey + localDateTime).getBytes()));
        String requestContentJsonString = JSONObject.toJSONString(params);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("appId", appId);
        httpHeaders.add("encode", "UTF-8");
        byte[] encrypt = AES.encrypt(requestContentJsonString.getBytes("UTF-8"), secretkey.getBytes(), algorithm);
        HttpEntity<Object> httpEntity = new HttpEntity<>(encrypt, httpHeaders);


        ResponseEntity<String> post = restTemplate.postForEntity(url, httpEntity, String.class);
        System.out.println(post.getStatusCode().value());
        System.out.println(post);

    }

    /**
     * MAS  V
     */
    @Test
    public void masSend() {

    }

    /**
     * 上海沃淘科技
     */
    @Test
    public void shwtSend() {
        String url = "http://122.144.203.28:8000/api/mt.ashx?account=110113&pswd=H65m1H4f&msg=【成都工商】短信测试&pn=18280045913";
        ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
        System.out.println(entity);

        String responseUrl = "http://122.144.203.28:8000/api/mo.ashx?account=110113&pswd=H65m1H4f";
        System.out.println(restTemplate.getForObject(responseUrl, String.class));

    }

    /**
     * 您好！接口访问地址：http://www.yescloudtree.cn:28009 用户名：cdcs 密码：cdcs0428（接口需要MD5加密大写32位）
     * 另访问接口需要鉴权公网IP访问，也麻烦提供下。
     *
     *
     * 商业信息通信 X
     */
    @Test
    public void syxxSend() throws UnsupportedEncodingException {

        System.out.println(DigestUtils.md5DigestAsHex("cdcs0428".getBytes()).toUpperCase());
        String url="http://www.yescloudtree.cn:28009";
        params.put("Action","sendsms");
        params.put("UserName","cdcs");
        //118.114.255.213 gd
        //119.6.102.87
        params.put("Password",DigestUtils.md5DigestAsHex("cdcs0428".getBytes()).toUpperCase());
        params.put("Mobile","18280045913");
        params.put("Message",new BASE64Encoder().encode("短信测试".getBytes()));
        String s = restTemplate.postForObject(url, params, String.class);
        System.out.println(new String(s.getBytes("iso-8859-1"),"UTF-8"));

    }



}
