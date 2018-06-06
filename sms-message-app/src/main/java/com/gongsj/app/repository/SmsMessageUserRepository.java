package com.gongsj.app.repository;

import com.gongsj.app.entity.SmsMessageUser;
import com.gongsj.core.domain.MessageUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SmsMessageUserRepository extends MongoRepository<SmsMessageUser, String> {
    List<SmsMessageUser> findByUserStatusNot(MessageUser.SmsUserStatus userStatus);
}
