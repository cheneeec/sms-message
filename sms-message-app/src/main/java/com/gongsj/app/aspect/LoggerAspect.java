package com.gongsj.app.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggerAspect {
    @Pointcut("execution(public * com.gongsj.core.sender.AbstractBasicSmsMessageSender+.sendMessage(..))||" +
            "execution(public * com.gongsj.app.service..*.*(..))")
    public void declareAspectPoint() {}

    @Before("execution(public * com.gongsj.app.api..*.*(..))")
    public void doLogBeforeRequest(JoinPoint joinPoint) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
        log.trace("====================================================================");
        log.trace("URL->{}；IP->{}；PORT->{}；HTTP_METHOD->{}", request.getRequestURL().toString(), request.getRemoteAddr(), request.getRemotePort(), request.getMethod());
        log.trace("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.trace("ARGS : " + Arrays.toString(joinPoint.getArgs()));
        log.trace("====================================================================");
    }

    @Around("declareAspectPoint()")
    public Object doServiceAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String methodName = proceedingJoinPoint.getSignature().getDeclaringTypeName()+"."+proceedingJoinPoint.getSignature().getName();
        Object[] methodArgs = proceedingJoinPoint.getArgs();
        log.trace("CLASS_METHOD->{}", methodName);
        log.trace("ARGS->{}", Arrays.toString(methodArgs));
        Object result;
        try {
            result = proceedingJoinPoint.proceed();
            log.trace("method:{} was successfully executed;result:{}", methodName, result);
        } catch (Throwable throwable) {
            log.error("method:{} failed to execute,error:{}", methodName, throwable.getMessage());
            throw throwable;
        }
        return result;
    }

}
