package com.gongsj.app.configuration;

import com.gongsj.app.entity.SmsMessageRecord;
import com.gongsj.app.repository.MessageRecordRepository;
import com.gongsj.app.sender.LtSmsMessageSender;
import com.gongsj.app.sender.YdSmsMessageSender;
import com.gongsj.core.property.SmsPlatformProperties;
import com.gongsj.core.sender.PersistentSaveRepository;
import com.gongsj.core.sender.SmsMessageSender;
import com.gongsj.core.sender.manager.MessageSenderManager;
import com.gongsj.core.sender.manager.SmsMessageSenderManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class MessageSenderConfiguration {

    @Value("${gongsj.sms.platform.default:yd}")
    private String defaultPlatformPrefix;



    @Bean
    public MessageSenderManager messageSenderManager(SmsPlatformProperties ltSmsPlatformProperties,
                                                     SmsPlatformProperties ydSmsPlatformProperties,
                                                     MessageRecordRepository messageRecordRepository) {

        PersistentSaveRepository persistentSaveRepository = (messageRecord) -> {
            SmsMessageRecord record = new SmsMessageRecord();
            BeanUtils.copyProperties(messageRecord, record);
            messageRecordRepository.save(record);
        };


        Map<String, SmsMessageSender> messageSenderMap = new HashMap<>(2);


        LtSmsMessageSender ltSmsMessageSender = new LtSmsMessageSender(persistentSaveRepository, ltSmsPlatformProperties);
        YdSmsMessageSender ydSmsMessageSender = new YdSmsMessageSender(persistentSaveRepository, ydSmsPlatformProperties);
        messageSenderMap.put("yd", ydSmsMessageSender);
        messageSenderMap.put("lt", ltSmsMessageSender);

        SmsMessageSenderManager messageSenderManager = new SmsMessageSenderManager(messageSenderMap);

        messageSenderManager.setCurrentMessageSender(messageSenderMap.getOrDefault(defaultPlatformPrefix, ydSmsMessageSender));

        return messageSenderManager;
    }
}
