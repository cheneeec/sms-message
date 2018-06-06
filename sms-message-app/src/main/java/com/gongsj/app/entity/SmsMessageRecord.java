package com.gongsj.app.entity;

import com.gongsj.core.domain.MessageRecord;
import org.springframework.data.annotation.Id;

public class SmsMessageRecord extends MessageRecord {
    @Override
    @Id
    public void setId(String id) {
        super.setId(id);
    }
}
