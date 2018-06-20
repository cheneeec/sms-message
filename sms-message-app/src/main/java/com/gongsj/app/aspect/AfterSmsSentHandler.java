package com.gongsj.app.aspect;

import com.gongsj.app.entity.NotifyContent;
import com.gongsj.app.entity.SmsMessageUser;
import com.gongsj.app.repository.NotifyContentRepository;
import com.gongsj.app.service.SmsMessageUserService;
import com.gongsj.core.SmsSimpleSendResponse;
import com.gongsj.core.exception.PlatArrearsException;
import com.gongsj.core.sender.manager.MessageSenderManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Aspect
@Component
@Slf4j
public class AfterSmsSentHandler implements InitializingBean {

    private final SmsMessageUserService messageUserService;

    private final MessageSenderManager messageSenderManager;

    private final NotifyContentRepository notifyContentRepository;

    @Value("${notifyContent}")
    private String defaultNotifyContent;

    public AfterSmsSentHandler(SmsMessageUserService messageUserService, MessageSenderManager messageSenderManager, NotifyContentRepository notifyContentRepository) {
        this.messageUserService = messageUserService;
        this.messageSenderManager = messageSenderManager;
        this.notifyContentRepository = notifyContentRepository;
    }


    @AfterReturning(value = "execution(public * com.gongsj.app.api..*.*(..))", returning = "smsSimpleSendResponse")
    public void sentHandle(JoinPoint joinPoint, SmsSimpleSendResponse smsSimpleSendResponse) throws PlatArrearsException {
        //获取用户ID
        String id = joinPoint.getArgs()[0].toString();
        SmsMessageUser messageUser = messageUserService.get(id);


        int consume = smsSimpleSendResponse.getConsume();

        //是否有必要进行短信提醒
        boolean principalPhoneInvalid = StringUtils.hasText(messageUser.getPrincipalPhone());

        //判断短信数是否超过
        boolean excess = (messageUser.getOwn() * ((double) messageUser.getWarningRate() / 100)) > messageUser.getUsable();

        //判断日期
        boolean dateArrived = dateArrived(messageUser);


        if (principalPhoneInvalid && excess && dateArrived) {
            executeNotify(messageUser);
            consume++;
        }
        messageUser.setHistorySpend(messageUser.getHistorySpend() + consume);

        messageUser.setSpend(messageUser.getSpend() + consume);
        messageUserService.save(messageUser);

    }

    private static boolean dateArrived(SmsMessageUser messageUser) {
        List<Date> lastWarnDates = Optional.ofNullable(messageUser.getLastWarnDate()).orElse(new ArrayList<>());
        Date now = Calendar.getInstance().getTime();
        if (CollectionUtils.isEmpty(lastWarnDates)) {
            lastWarnDates.add(now);
            messageUser.setLastWarnDate(lastWarnDates);
            return true;
        }
        Date lastWarnDate = lastWarnDates.get(lastWarnDates.size() - 1);

        Date date = DateUtils.addDays(lastWarnDate, messageUser.getWarningInterval());
        if (date.before(now)) {
            lastWarnDates.add(now);
            messageUser.setLastWarnDate(lastWarnDates);
            return true;
        }
        return false;
    }

    private void executeNotify(SmsMessageUser messageUser) throws PlatArrearsException {
        String notifyContent = defaultNotifyContent
                .replaceAll(":systemName", messageUser.getSystemName())
                .replaceAll(":principalName", messageUser.getPrincipalName())
                .replaceAll(":now", LocalDateTime.now().toString())
                .replaceAll(":spend", String.valueOf(messageUser.getSpend()))
                .replaceAll(":usable", String.valueOf(messageUser.getUsable()))
                .replaceAll("bf", "%");

        SmsSimpleSendResponse simpleResponse = messageSenderManager.sendMessage(messageUser.getId(), messageUser.getPrincipalPhone(), notifyContent);
        if (simpleResponse.getSuccess()) {
           log.info("Reminder {},id={},Insufficient usable,notify content:{}", messageUser.getSystemName(), messageUser.getId(),notifyContent);
        }

    }

    @Override
    public void afterPropertiesSet() {
        NotifyContent content = notifyContentRepository.findOne(NotifyContentRepository.NOTIFY_CONTENT_ID);
        if (content == null) {
            notifyContentRepository.save(new NotifyContent(NotifyContentRepository.NOTIFY_CONTENT_ID, defaultNotifyContent));
        }
    }
}
