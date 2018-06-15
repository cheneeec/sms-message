package com.gongsj.core.sender;

import com.gongsj.core.SmsSimpleSendResponse;
import com.gongsj.core.property.SmsPlatformProperties;

/**
 * 只做数据库的记录，不做实际的发送。
 */
public class MockSmsMessageSender extends AbstractBasicSmsMessageSender {

    public MockSmsMessageSender(PersistentSaveRepository persistentSaveRepository, String platform) {
        this(persistentSaveRepository, new SmsPlatformProperties(platform));
    }

    public MockSmsMessageSender(PersistentSaveRepository persistentSaveRepository, SmsPlatformProperties platformProperties) {
        super(persistentSaveRepository, platformProperties);
    }

    @Override
    protected SmsSimpleSendResponse doSendMessage(String phoneNumber, String content, String templateId) {
        return new SmsSimpleSendResponse(true, null);
    }
}
