package com.gongsj.app.Interceptor;


import com.gongsj.app.entity.SmsMessageUser;
import com.gongsj.app.exception.IllegalIpAddressException;
import com.gongsj.app.exception.IllegalSmsUserException;


import com.gongsj.app.entity.NotifyContent;
import com.gongsj.app.repository.NotifyContentRepository;
import com.gongsj.app.service.SmsMessageUserService;
import com.gongsj.core.SmsSimpleSendResponse;
import com.gongsj.core.domain.MessageUser;
import com.gongsj.core.exception.PlatArrearsException;
import com.gongsj.core.sender.manager.MessageSenderManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Component
@Slf4j
public class SmsMessageHandlerInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

    private final SmsMessageUserService messageUserService;

    private final ThreadLocal<SmsMessageUser> local = new ThreadLocal<>();

    private final MessageSenderManager messageSenderManager;

    private final NotifyContentRepository notifyContentRepository;

    @Value("${notifyContent}")
    private String defaultNotifyContent;

    public SmsMessageHandlerInterceptor(SmsMessageUserService messageUserService, MessageSenderManager messageSenderManager, NotifyContentRepository notifyContentRepository) {
        this.messageUserService = messageUserService;
        this.messageSenderManager = messageSenderManager;
        this.notifyContentRepository = notifyContentRepository;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SmsMessageUser messageUser = messageUserService.get(extractMessageUserId(request));
        local.set(messageUser);
        if (messageUser == null) {
            throw new IllegalSmsUserException("非法的用户");
        }
        if (messageUser.getUserStatus() == MessageUser.SmsUserStatus.DISABLED) {
            throw new IllegalSmsUserException("该账号已被禁用");
        }
        List<String> ips = messageUser.getIps();
        String requestIpAddress = getIpAddress(request);
        if (CollectionUtils.isEmpty(ips) || !ips.contains(requestIpAddress)) {
            throw new IllegalIpAddressException("非法的IP地址：" + requestIpAddress + "，请联系管理员添加");
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        SmsMessageUser messageUser = local.get();

        messageUser.setSpend(messageUser.getSpend() + 1);

        messageUser.setHistorySpend(messageUser.getHistorySpend() + 1);

        //是否有必要进行短信提醒
        boolean principalPhone = StringUtils.hasText(messageUser.getPrincipalPhone());
        //判断短信数是否超过
        boolean excess = (messageUser.getOwn() * ((double) messageUser.getWarningRate() / 100)) > messageUser.getUsable();
        //判断日期
        List<Date> lastWarnDates = messageUser.getLastWarnDate();
        boolean dateArrived = dateArrived(messageUser, lastWarnDates);
        if (principalPhone && excess && dateArrived) {
            executeNotify();
        }
        messageUserService.save(messageUser);
        //移除本地
        local.remove();
    }

    private static boolean dateArrived(SmsMessageUser messageUser, List<Date> lastWarnDates) {
        if (CollectionUtils.isEmpty(lastWarnDates)) {
            return true;
        }
        Date lastWarnDate = lastWarnDates.get(lastWarnDates.size() - 1);
        Date now = Calendar.getInstance().getTime();
        Date date = DateUtils.addDays(lastWarnDate, messageUser.getWarningInterval());
        return date.after(now);
    }

    private void executeNotify() throws PlatArrearsException {
        SmsMessageUser messageUser = local.get();
        String notifyContent = defaultNotifyContent
                .replaceAll(":systemName", messageUser.getSystemName())
                .replaceAll(":principalName", messageUser.getPrincipalName())
                .replaceAll(":now", LocalDateTime.now().toString())
                .replaceAll(":spend", String.valueOf(messageUser.getSpend()))
                .replaceAll(":usable", String.valueOf(messageUser.getUsable()))
                .replaceAll("bf", "%");
        SmsSimpleSendResponse simpleResponse = messageSenderManager.sendMessage(messageUser.getId(), messageUser.getPrincipalPhone(), notifyContent);
        if (simpleResponse.getSuccess()) {
            log.info("Reminder {},id={},Insufficient usable", messageUser.getSystemName(), messageUser.getId());
        }
    }


    private String extractMessageUserId(HttpServletRequest request) {
        String[] requestUris = request.getRequestURI().split("/");
        return requestUris[requestUris.length - 1];
    }


    private final static String getIpAddress(HttpServletRequest request) {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
        String ip = request.getHeader("X-Forwarded-For");

        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");

            }
            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");

            }
            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");


            }
            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");

            }
            if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (String strIp : ips) {
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    @Override
    public void afterPropertiesSet() {
        NotifyContent content = notifyContentRepository.findOne(NotifyContentRepository.NOTIFY_CONTENT_ID);
        if (content == null) {
            notifyContentRepository.save(new NotifyContent(NotifyContentRepository.NOTIFY_CONTENT_ID, defaultNotifyContent));
        }

    }
}
