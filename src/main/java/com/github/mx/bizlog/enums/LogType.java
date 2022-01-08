package com.github.mx.bizlog.enums;

/**
 * 类型
 * <p>
 * Create by max on 2021/08/13
 **/
public enum LogType {
    /**
     * 前端调用
     */
    WEB,
    /**
     * 任务触发
     */
    JOB,
    /**
     * HTTP调用
     */
    HTTP,
    /**
     * RPC调用
     */
    RPC,
    /**
     * 消息中间件
     */
    MQ,
    /**
     * 本地调用
     */
    LOCAL,
    /**
     * 其他
     */
    OTHER,
    ;
}
