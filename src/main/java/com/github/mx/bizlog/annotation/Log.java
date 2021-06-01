package com.github.mx.bizlog.annotation;

import com.github.mx.bizlog.extend.defaults.DefaultLogOperator;

import java.lang.annotation.*;

/**
 * 注解到需要拦截的方法上
 * <p>
 * Create by max on 2021/02/27
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Log {

    /**
     * 业务主键
     */
    String bizId();

    /**
     * 成功时信息
     */
    String success();

    /**
     * 失败时信息
     */
    String fail() default "";

    /**
     * 操作人
     */
    String operatorId() default DefaultLogOperator.DEFAULT_OPERATOR_ID;

    /**
     * 分类
     */
    String category() default "";

    /**
     * 自定义信息
     */
    String content() default "";
}