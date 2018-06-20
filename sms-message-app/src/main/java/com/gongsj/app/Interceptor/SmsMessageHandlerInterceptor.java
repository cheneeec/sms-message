package com.gongsj.app.Interceptor;


import com.gongsj.app.entity.SmsMessageUser;
import com.gongsj.app.exception.IllegalIpAddressException;
import com.gongsj.app.exception.IllegalSmsUserException;


import com.gongsj.app.service.SmsMessageUserService;
import com.gongsj.core.domain.MessageUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class SmsMessageHandlerInterceptor extends HandlerInterceptorAdapter {

    private final SmsMessageUserService messageUserService;


    @Value("${notifyContent}")
    private String defaultNotifyContent;

    public SmsMessageHandlerInterceptor(SmsMessageUserService messageUserService) {
        this.messageUserService = messageUserService;

    }


    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        SmsMessageUser messageUser = messageUserService.get(((Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).get("sysNo"));

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


}
