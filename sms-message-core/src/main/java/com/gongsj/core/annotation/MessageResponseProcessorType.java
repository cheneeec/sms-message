package com.gongsj.core.annotation;


import com.gongsj.core.parse.SmsHttpResponseParser;

import java.lang.annotation.*;

/**
 * 指定平台的响应处理结果的类型。
 * <p>
 * 当默认提供的<code>json，params</code>不能满足要求时，可以实现{@link SmsHttpResponseParser}接口。
 * 并且用该注解指定该类型，然后将其交由<code>Spring</code>容器管理。
 * </p>
 * @see SmsHttpResponseParser
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageResponseProcessorType {
    String value() default "";
}
