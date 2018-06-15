package com.gongsj.app.entity;


import com.gongsj.core.domain.MessageUser;
import org.springframework.data.annotation.Id;

public class SmsMessageUser extends MessageUser {

    @Override
    @Id
    public void setId(String id) {
        super.setId(id);
    }

}
