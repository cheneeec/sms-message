package com.gongsj.core.sender;


import com.gongsj.core.SmsRemainingNumberResponse;
import com.gongsj.core.SmsSimpleSendResponse;
import com.gongsj.core.exception.PlatArrearsException;


public interface SmsMessageSender {

    /**
     * 发送短信
     * @param sysName 系统编号
     * @param phoneNumber  手机号
     * @param content 内容
     * @param templateId 模板ID
     * @return {@link SmsSimpleSendResponse}
     * @throws PlatArrearsException 当平台欠费时，抛出的异常。
     */
    SmsSimpleSendResponse sendMessage(String sysName, String phoneNumber, String content, String templateId) throws PlatArrearsException;

    /**
     *
     * @param sysName
     * @param phoneNumber
     * @param content
     * @return
     * @throws PlatArrearsException
     * @see SmsMessageSender#sendMessage(String, String, String,String)
     */
    SmsSimpleSendResponse sendMessage(String sysName, String phoneNumber, String content) throws PlatArrearsException;

    /**
     *
     * @return 获取当前的发送平台
     */
    String getPlatform();

    /**
     *
     * @return 获取剩余的短信数
     */
    SmsRemainingNumberResponse getRemainingNumber();


}
