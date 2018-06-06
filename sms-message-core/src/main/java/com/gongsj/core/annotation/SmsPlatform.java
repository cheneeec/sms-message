package com.gongsj.core.annotation;


import java.lang.annotation.*;

/**
 * 用于标记在{@link com.gongsj.core.sender.AbstractSmsMessageSender}的子类上面，
 * 当子类的类名不能决定注入哪个平台的属性时，就由该注解来决定。
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SmsPlatform {
    String value() default "";
}
