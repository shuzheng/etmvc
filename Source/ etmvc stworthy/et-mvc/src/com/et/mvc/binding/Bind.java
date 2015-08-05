package com.et.mvc.binding;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 绑定请求参数注解，用于指示请求参数的前缀，如：<br/>
 * <pre>
 * public void save(@Bind(prefix="user")User user) throws Exception{
 * }
 * </pre>
 * HTTP传递过来的参数必须是user.code,user.name才能绑定到user对象
 * @author stworthy
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bind {
    String prefix() default "";
}
