package com.github.mx.bizlog.bean;

import com.github.mx.bizlog.extend.defaults.DefaultLogOperator;
import com.github.mx.nacos.config.core.ConfigFactory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Create by max on 2021/02/27
 **/
@Data
@Builder
@AllArgsConstructor
public class LogRecord {
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 功能名称
     */
    private String title;
    /**
     * 日志类型
     */
    private String logType;
    /**
     * 分类
     */
    private String category;
    /**
     * 业务ID
     */
    private String bizId;
    /**
     * 操作人ID
     */
    private String operatorId;
    /**
     * 是否成功
     */
    @Builder.Default
    private Boolean success = true;
    /**
     * 具体动作
     */
    private String action;
    /**
     * 详情
     */
    private String detail;

    private Long startTime;
    private Long endTime;
    private Long createTime;
    /**
     * 描述
     */
    private String content;

    public LogRecord(String title) {
        setDefault();
    }

    public LogRecord(String title, String bizId) {
        this.setDefault();
        this.title = title;
        this.bizId = bizId;
    }

    public LogRecord(String title, String bizId, String content) {
        this.setDefault();
        this.bizId = bizId;
        this.content = content;
    }

    public LogRecord(String title, String bizId, String category, String content) {
        this.setDefault();
        this.bizId = bizId;
        this.category = category;
        this.content = content;
    }

    private void setDefault() {
        this.success = true;
        this.appName = ConfigFactory.getApplicationName();
        this.operatorId = DefaultLogOperator.DEFAULT_OPERATOR_ID;
        this.createTime = System.currentTimeMillis();
        this.endTime = System.currentTimeMillis();
        this.createTime = ObjectUtils.defaultIfNull(this.createTime, System.currentTimeMillis());
    }
}
