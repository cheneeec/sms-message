package com.gongsj.core.sender.manager;


import com.gongsj.core.SmsRemainingNumberResponse;
import com.gongsj.core.SmsSimpleSendResponse;
import com.gongsj.core.exception.PlatArrearsException;
import com.gongsj.core.sender.SmsMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 协调所有的{@link SmsMessageSender}保证目前可用的短信平台，都是可用的机器。
 */
@Slf4j
public class SmsMessageSenderManager implements MessageSenderManager {

    //存放所有的短信发送平台
    private final Map<String, SmsMessageSender> messageSenderContexts;
    //目前无效的短信发送平台
    private final Map<String, SmsMessageSender> invalidMessageSenderContexts;
    //目前有效的短信发送平台
    private final Map<String, SmsMessageSender> validMessageSenderContexts;
    //
    private SmsMessageSender currentMessageSender;


    public SmsMessageSenderManager(Map<String, SmsMessageSender> messageSenderContexts) {
        Assert.notEmpty(messageSenderContexts, "messageSenderContexts is empty or null");
        this.messageSenderContexts = Collections.unmodifiableMap(messageSenderContexts);
        this.validMessageSenderContexts = new ConcurrentHashMap<>(messageSenderContexts);
        this.invalidMessageSenderContexts = new ConcurrentHashMap<>();
    }

    private String getKeyByPlatform(SmsMessageSender messageSender) {
        for (Map.Entry<String, SmsMessageSender> messageSenderEntry : messageSenderContexts.entrySet()) {
            if (messageSenderEntry.getValue().equals(messageSender)) {
                return messageSenderEntry.getKey();
            }
        }
        throw new IllegalStateException("illegal platform:" + messageSender.getPlatform());
    }

    @Override
    public SmsSimpleSendResponse sendMessage(String sysName, String phoneNumber, String content, String templateId) {
        try {
            return currentMessageSender.sendMessage(sysName, phoneNumber, content, templateId);
        } catch (PlatArrearsException e) {
            if (switchPlatform(true)) {
                return sendMessage(sysName, phoneNumber, content, templateId);
            }
            //已经没有有效的发送平台
            return new SmsSimpleSendResponse(false, "error:all smsMessageSender can not be used");
        }
    }


    @Override
    public SmsSimpleSendResponse sendMessage(String sysName, String phoneNumber, String content) {
        return sendMessage(sysName, phoneNumber, content, null);
    }

    @Override
    public String getPlatform() {
        return currentMessageSender.getPlatform();
    }

    @Override
    public SmsRemainingNumberResponse getRemainingNumber() {
        return currentMessageSender.getRemainingNumber();
    }

    @Override
    public SmsMessageSender getCurrentMessageSender() {
        return currentMessageSender;
    }

    @Override
    public SmsMessageSender getPlatform(String platformPrefix) {
        Assert.hasText(platformPrefix, "platformPrefix is empty or null");
        return messageSenderContexts.get(platformPrefix);
    }

    @Override
    public void setCurrentMessageSender(SmsMessageSender messageSender) {
        //如果指定的平台为有效的平台
        if (!validMessageSenderContexts.containsValue(messageSender)) {
            String key = getKeyByPlatform(messageSender);
            validMessageSenderContexts.put(key, invalidMessageSenderContexts.remove(key));
        }
        currentMessageSender = messageSender;
    }


    @Override
    public boolean refresh() {
        validMessageSenderContexts.putAll(messageSenderContexts);
        invalidMessageSenderContexts.clear();
        log.info("All smsMessageSender has been re-added");
        return true;
    }

    @Override
    public SmsRemainingNumberResponse getRemainingNumber(String platformPrefix) {
        if (StringUtils.isBlank(platformPrefix)) {
            return getRemainingNumber();
        }
        return messageSenderContexts.get(platformPrefix).getRemainingNumber();
    }

    @Override
    public boolean switchPlatform(boolean invalidCurrentPlatform) {
        if (CollectionUtils.isEmpty(validMessageSenderContexts) || validMessageSenderContexts.size() < 2) {
            log.error("Failed to switch because there is no platform available");
            return false;
        }
        String currentMessageSenderKey = getKeyByPlatform(currentMessageSender);

        if (invalidCurrentPlatform) {
            invalidMessageSenderContexts.put(currentMessageSenderKey, currentMessageSender);
            validMessageSenderContexts.remove(currentMessageSenderKey);
        }
        SmsMessageSender newSmsMessageSender = null;

        for (SmsMessageSender messageSender : validMessageSenderContexts.values()) {
            if (!messageSender.equals(currentMessageSender)) {
                newSmsMessageSender = messageSender;
                break;
            }
        }
        if (newSmsMessageSender == null) {
            log.error("Failed to switch because there is no platform available");
            return false;
        }
        log.info("the current SmsMessageSender has been replaced by {}", newSmsMessageSender.getPlatform());
        log.debug("the current invalid SmsMessageSenders->{},valid SmsMessageSender->{}", invalidMessageSenderContexts, validMessageSenderContexts);
        setCurrentMessageSender(newSmsMessageSender);
        return true;
    }


}
