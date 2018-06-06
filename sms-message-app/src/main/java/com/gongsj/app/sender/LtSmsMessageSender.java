package com.gongsj.app.sender;

import com.gongsj.core.property.SmsPlatformProperties;
import com.gongsj.core.sender.AbstractBasicSmsMessageSender;
import com.gongsj.core.sender.PersistentSaveRepository;


public class LtSmsMessageSender extends AbstractBasicSmsMessageSender {


    public LtSmsMessageSender(PersistentSaveRepository persistentSaveRepository, SmsPlatformProperties platformProperties) {
        super(persistentSaveRepository, platformProperties);
    }
}
