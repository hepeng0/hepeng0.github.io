package com.hepeng.example.common.lock;

import com.hepeng.example.common.constants.CommonConstants;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 * ---------------------------------------------------------------------------------------------------
 * 注：被注解方法不能private，其余三种均可
 * ---------------------------------------------------------------------------------------------------
 * @GLock
 * @GLock("mid")
 * @GLock("#id")
 * @GLock(key="#id")
 * @GLock(key="#userMsg.name")
 * @GLock(key="'myapp:' + #userMsg.name")
 * public CommResult<Map<String, Object>> prop(int id, UserMsg userMsg){ // do business... }
 * ---------------------------------------------------------------------------------------------------
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GLock {
    @AliasFor("key")
    String value() default "";

    @AliasFor("value")
    String key() default "";

    String appname() default CommonConstants.APPLICATION_NAME;

    /**
     * 锁等待时间
     */
    long waitTime() default -1;

    /**
     * 锁释放时间
     */
    long leaseTime() default -1;

    /**
     * 时间单位，默认：秒
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 加锁失败时回调的方法名
     * --------------------------------------------------------------------------------
     * 未传值则不回调，传值时其方法参数应与加锁的方法参数完全相同（方法名和返回值可不同）
     * --------------------------------------------------------------------------------
     */
    String fallbackMethod() default "";
}