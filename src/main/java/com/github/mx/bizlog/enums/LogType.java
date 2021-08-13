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
     * RPC调用
     */
    RPC,
    /**
     * 消息中间件
     */
    MQ,
    /**
     * 其他
     */
    OTHER,
    ;
}
