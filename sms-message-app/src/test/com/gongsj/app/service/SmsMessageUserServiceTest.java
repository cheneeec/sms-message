package com.gongsj.app.service;

import com.gongsj.app.SmsApplication;
import com.gongsj.app.entity.SmsMessageUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

@SpringBootTest(classes = SmsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class SmsMessageUserServiceTest {
    @Autowired
    SmsMessageUserService messageUserService;

    @Test
    public void findByUserStatusNot() {
    }

    @Test
    public void save() {
        SmsMessageUser messageUser = new SmsMessageUser();
        messageUser.setId("test01");
        messageUser.setSystemName("短信测试");
        messageUser.setIps(Arrays.asList("127.0.0.1", "0:0:0:0:0:0:0:1"));
        messageUser.setPrincipalName("大红");
        messageUser.setPrincipalPhone("18280045913");
        messageUser.setOwn(7);
        messageUserService.save(messageUser);
    }

    @Test
    public void create() {
        SmsMessageUser messageUser = new SmsMessageUser();
        messageUser.setId("test01");
        messageUser.setSystemName("短信测试");
        messageUser.setIps(Collections.singletonList("127.0.0.1"));
        messageUser.setPrincipalName("大红");
        messageUser.setPrincipalPhone("18280045913");
        messageUser.setOwn(9999);
        messageUserService.create(messageUser);
    }

    @Test
    public void get() {
    }

    @Test
    public void findByPage() {
    }

    @Test
    public void findAll() {
    }

    @Test
    public void findAll1() {
    }

    @Test
    public void exist() {
    }
}