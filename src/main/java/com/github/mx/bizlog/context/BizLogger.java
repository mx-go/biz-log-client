package com.github.mx.bizlog.context;

import com.alibaba.fastjson.JSON;
import com.github.mx.bizlog.bean.LogRecord;
import com.github.mx.bizlog.enums.LogType;
import com.github.mx.bizlog.enums.ReportType;
import com.github.mx.nacos.config.core.util.ConfigHelper;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.boot.logging.LogLevel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * 业务日志采集上报
 * <p>
 * Create by max on 2022/04/06
 **/
public class BizLogger {

    private static final Logger log = LoggerFactory.getLogger(BizLogger.class);
    private static final String TITLE = "业务日志采集上报";
    private static final String DELIMITER = " | ";

    public static void warn(String bizId, String message) {
        log.warn(message);
        log(bizId, LogLevel.WARN, message);
    }

    /**
     * 输出warn日志并采集上报
     *
     * @param bizId     业务主键
     * @param format    带有占位符的字符串
     * @param arguments 参数
     */
    public static void warn(String bizId, String format, Object... arguments) {
        log.warn(format, arguments);
        log(bizId, LogLevel.WARN, MessageFormatter.arrayFormat(format, arguments).getMessage());
    }

    public static void error(String bizId, String message) {
        log.error(message);
        log(bizId, LogLevel.ERROR, message);
    }

    /**
     * 输出error日志并采集上报
     *
     * @param bizId     业务主键
     * @param format    带有占位符的字符串
     * @param arguments 参数
     */
    public static void error(String bizId, String format, Object... arguments) {
        log.error(format, arguments);
        log(bizId, LogLevel.ERROR, MessageFormatter.arrayFormat(format, arguments).getMessage());
    }

    /**
     * 业务异常日志采集
     *
     * @param bizId   业务主键
     * @param level   日志级别
     * @param message 日志信息
     */
    private static void log(String bizId, LogLevel level, String message) {
        try {
            StackTraceElement element = new Throwable().getStackTrace()[2];
            String line = LocalDateTime.now(ZoneId.systemDefault()) + DELIMITER + level + DELIMITER + "[" + Thread.currentThread().getName() + "]" + DELIMITER + "[" + element.getClassName() + "#" + element.getMethodName() + "]" + "-" + element.getLineNumber() + DELIMITER + message;
            String hostName = ConfigHelper.getHostName(), serverIp = ConfigHelper.getServerIp();
            Map<String, Object> args = ImmutableMap.of("line", line, "level", level, "hostName", hostName, "serverIp", serverIp);

            LogRecord record = new LogRecord();
            record.setTitle(TITLE);
            record.setBizId(bizId);
            record.setReportType(ReportType.BIZ.getType());
            record.setLogType(LogType.LOCAL);
            record.setDetail(message);
            record.setContent(JSON.toJSONString(args));
            BizLog.log(record);
        } catch (Exception e) {
            log.error("report biz log failed. bizId:{}, message:{}", bizId, message);
        }
    }
}