package com.github.mx.bizlog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 上报类型
 * <p>
 * Create by max on 2022/04/06
 **/
@AllArgsConstructor
@Getter
public enum ReportType {
    /**
     * 审计日志
     */
    AUDIT(1),
    /**
     * 业务日志
     */
    BIZ(2);
    /**
     * 类型
     */
    private final int type;

    public static ReportType of(int type) {
        return Arrays.stream(ReportType.values()).filter(reportType -> reportType.getType() == type).findFirst().orElseThrow(IllegalArgumentException::new);
    }
}