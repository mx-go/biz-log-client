package com.github.mx.bizlog.bean;

import com.github.mx.bizlog.enums.LogType;
import com.github.mx.bizlog.enums.ReportType;
import com.github.mx.bizlog.extend.defaults.DefaultLogOperator;
import com.github.mx.nacos.config.core.ConfigFactory;
import com.google.common.base.Joiner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Create by max on 2021/02/27
 **/
@Data
@Builder
@AllArgsConstructor
public class LogRecord {
    /**
     * 系统编码
     */
    private String systemCode;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 功能名称
     */
    private String title;
    /**
     * 上报类型，默认审计日志。不对客户端开放
     *
     * @see com.github.mx.bizlog.enums.ReportType
     */
    @Builder.Default
    private Integer reportType = ReportType.AUDIT.getType();
    /**
     * 日志类型
     */
    private LogType logType;
    /**
     * 分类
     */
    private String category;
    /**
     * 业务ID。非空
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

    private Long startTime = System.currentTimeMillis();
    private Long endTime = System.currentTimeMillis();
    private Long createTime = System.currentTimeMillis();
    /**
     * 描述
     */
    private String content;

    /**
     * 拼接bizId
     */
    public void spliceBizId(String... keys) {
        this.bizId = Joiner.on("-").join(keys);
    }

    public void setBizIds(List<String> bizIds) {
        if (bizIds != null && !bizIds.isEmpty()) {
            this.bizId = StringUtils.join(bizIds, ",");
        }
    }

    public <T> void setBizIds(List<T> dataList, Function<T, String> key1, Function<T, String> key2) {
        if (dataList != null && !dataList.isEmpty()) {
            this.setBizIds(dataList.stream().map(e -> Joiner.on("-").join(key1.apply(e), key2.apply(e))).collect(Collectors.toList()));
        }
    }

    public LogRecord() {
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
        this.reportType = ReportType.AUDIT.getType();
        this.operatorId = DefaultLogOperator.DEFAULT_OPERATOR_ID;
        this.createTime = System.currentTimeMillis();
        this.endTime = System.currentTimeMillis();
        this.createTime = ObjectUtils.defaultIfNull(this.createTime, System.currentTimeMillis());
    }
}
