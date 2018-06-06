package com.gongsj.app.api;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;


public class SmsMessageSenderControllerTest {

    RestTemplate restTemplate=new RestTemplate();
    @Test
    public void sendMessage() {

        String print = restTemplate.postForObject("http://171.221.172.20:7082/v1/api/sms/aa01?phoneNumber=18280045913&content=测试短信111", null, String.class);
        System.out.println(print);
    }

    //剩余短信数
    @Test
    public void sendMessage1() {

        String object = restTemplate.postForObject("http://sms.api.ums86.com:8899/sms/Api/SearchNumber.do", null, String.class);
        System.out.println(object);
    }
}