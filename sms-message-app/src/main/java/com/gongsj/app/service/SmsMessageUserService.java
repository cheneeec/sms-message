package com.gongsj.app.service;

import com.gongsj.app.entity.SmsMessageUser;
import com.gongsj.core.domain.MessageUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;


public interface SmsMessageUserService {


    List<SmsMessageUser> findByUserStatusNot(MessageUser.SmsUserStatus userStatus);

    SmsMessageUser save(SmsMessageUser messageUser);

    void create(SmsMessageUser messageUser);

    SmsMessageUser get(String id);

    Page<SmsMessageUser> findByPage(Pageable page);

    List<SmsMessageUser> findAll();

    List<SmsMessageUser> findAll(Set<String> ids);

    boolean exist(String id);

    void remove(String id);
}
