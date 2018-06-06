package com.gongsj.core.sender.manager;

import com.gongsj.core.SmsRemainingNumberResponse;
import com.gongsj.core.sender.SmsMessageSender;

public interface MessageSenderManager extends SmsMessageSender {
    /**
     * @return 获取当前正在使用的发送平台。（由配置文件中的内容决定）
     */
    SmsMessageSender getCurrentMessageSender();

    /**
     * @param platformPrefix 平台的前缀简写。
     * @return 指定的发送平台。
     */
    SmsMessageSender getPlatform(String platformPrefix);

    /**
     * 将当前的平台设定为指定的平台
     *
     * @param messageSender 指定的设置平台
     */
    void setCurrentMessageSender(SmsMessageSender messageSender);


    /**
     * 将弃用的平台重新弃用
     */
    boolean refresh();


    SmsRemainingNumberResponse getRemainingNumber(String platformPrefix);

    /**
     * 切换当前平台
     *
     * @return 成功切换
     */
    boolean switchPlatform(boolean invalidCurrentPlatform);


}
