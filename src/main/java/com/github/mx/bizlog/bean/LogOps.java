package com.github.mx.bizlog.bean;

import com.github.mx.bizlog.enums.LogType;
import lombok.Builder;
import lombok.Data;

/**
 * Create by max on 2021/02/27
 **/
@Data
@Builder
public class LogOps {
    private String title;
    private LogType logType;
    private String bizId;
    private String successLogTemplate;
    private String failLogTemplate;
    private String detail;
    private String operatorId;
    private String category;
    private String content;
    private String condition;
}