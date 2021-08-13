package com.github.mx.bizlog.annotation;

import com.github.mx.bizlog.enums.LogType;
import com.github.mx.bizlog.extend.defaults.DefaultLogOperator;

import java.lang.annotation.*;

/**
 * 注解到需要拦截的方法上
 * <p>
 * Create by max on 2021/02/27
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Log {

    /**
     * 功能名称
     * e.g：删除订单、修改订单
     */
    String title() default "";

    /**
     * 业务主键
     * 如订单ID
     */
    String bizId() default "";

    /**
     * 日志类型
     */
    LogType logType() default LogType.WEB;

    /**
     * 分类
     * 如操作日志、业务日志
     */
    String category() default "";

    /**
     * 成功时信息
     * 默认取返回值
     */
    String success() default "{{T(com.alibaba.fastjson.JSON).toJSONString(#_ret)}}}";

    /**
     * 失败时信息
     * 默认为失败信息
     */
    String fail() default "{{#_errorMsg}}";

    /**
     * 详情
     * 默认为入参
     */
    String detail() default "{{#_params}}";

    /**
     * 操作人
     * 默认为system
     */
    String operatorId() default DefaultLogOperator.DEFAULT_OPERATOR_ID;

    /**
     * 自定义信息
     */
    String content() default "";
}