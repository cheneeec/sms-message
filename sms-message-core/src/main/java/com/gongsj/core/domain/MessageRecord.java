package com.gongsj.core.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MessageRecord {

    private String id;

    private Date sendTime = new Date();//发送时间
    private String content; //发送内容
    private List<String> phoneNumber; //接受方的电话号码
    private String platformName;//发送短信的平台
    private String templateId;//短信的模板id（仅目前移动和联通都没有这个功能）
    private Long responseTime;//响应时间
    private Boolean success; //发送是否成功
    private String message;//记录消息，当发送失败时，记录失败原因

    private MessageUser messageUser;

}
