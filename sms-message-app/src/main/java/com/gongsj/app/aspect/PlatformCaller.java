package com.gongsj.app.aspect;

import com.gongsj.app.api.SmsMessageSenderController;
import com.gongsj.core.sender.SmsMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import java.lang.reflect.Method;
import java.util.*;



/**
 * 该类根据请求参数platform来决定使用哪个平台来发送消息,最终调用的仍然是{@link SmsMessageSenderController}。
 *
 * @see SmsMessageSenderController
 */
//@Aspect
//@Component
@Slf4j
public class PlatformCaller {

    private static final String MESSAGE_SENDER_BEAN_NAME_SUFFIX = "SmsMessageSender";

    private static final String MESSAGE_SEND_PROPERTY_NAME = "activeMessageSender";

    @Value("${gongsj.sms.platform.default:yd}")
    private String defaultPlatformPrefix;

    @Value("${gongsj.sms.platform.requestProperty:platform}")
    private String requestPlatformProperty;

    private final Map<String, SmsMessageSender> messageSenderContainer;

    private final Method messageSenderWriteMethod;

    private final Method messageSenderReadMethod;

    public PlatformCaller(Map<String, SmsMessageSender> messageSenderContainer) throws IntrospectionException {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(MESSAGE_SEND_PROPERTY_NAME, SmsMessageSenderController.class);
        this.messageSenderWriteMethod = propertyDescriptor.getWriteMethod();
        this.messageSenderReadMethod = propertyDescriptor.getReadMethod();
        this.messageSenderContainer = messageSenderContainer;
    }

    @Before("execution(public * com.gongsj.app.api.SmsMessageSenderController.sendMessage(..))")
    public void doServiceBefore(JoinPoint joinPoint) {

        SmsMessageSenderController messageController = (SmsMessageSenderController) joinPoint.getTarget();

        SmsMessageSender messageSenderPlatform = getRequestMessageSenderPlatform(getRequestPlatformPrefix());

        if (decideIsResetSenderPlatform(messageController, messageSenderPlatform)) {
            resetSmsMessageSenderIfNecessary(
                    messageController,
                    messageSenderPlatform
            );
        }
    }

    private boolean decideIsResetSenderPlatform(SmsMessageSenderController messageController, SmsMessageSender messageSenderPlatform) {
        return ReflectionUtils.invokeMethod(messageSenderReadMethod, messageController) != messageSenderPlatform;
    }


    private void resetSmsMessageSenderIfNecessary(SmsMessageSenderController targetMessageController, SmsMessageSender messageSender) {

        ReflectionUtils.invokeMethod(messageSenderWriteMethod, targetMessageController, messageSender);
    }


    private SmsMessageSender getRequestMessageSenderPlatform(String requestPlatformPrefix) {

        final String messageSenderPrefix = StringUtils.hasText(requestPlatformPrefix) ? requestPlatformPrefix : defaultPlatformPrefix;

        return this.messageSenderContainer.get(messageSenderPrefix + MESSAGE_SENDER_BEAN_NAME_SUFFIX);
    }

    private String getRequestPlatformPrefix() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        return request.getParameter("platform");
    }

/*    private String getTargetControllerMessageSenderFieldName(SmsMessageController messageController) {
        String messageSenderClassName = SmsMessageSender.class.getName();

        Set<String> messageSenderFieldNames = Arrays.stream(messageController.getClass().getDeclaredFields())
                .filter(sender -> sender.getType().getName().equals(messageSenderClassName))
                .map(Field::getName)
                .collect(toSet());

        if (messageSenderFieldNames.size() == 1) {
            return messageSenderFieldNames.iterator().next();
        }

        throw new IllegalStateException(String.format("You must have a field:%s in %s", messageSenderClassName, messageController.getClass().getName()));

    }*/
}
