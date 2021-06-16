package com.github.mx.bizlog.context;

import com.github.mx.bizlog.bean.LogRecord;
import com.github.mx.bizlog.extend.LogPersistence;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * 工具类
 * <p>
 * Create by max on 2021/06/01
 **/
@Slf4j
public class BizLog {

    private static LogPersistence logPersistence;

    public static void setLogPersistence(LogPersistence logPersistence) {
        BizLog.logPersistence = logPersistence;
    }

    public static <T, R> void log(T bizId, R content) {
        if (bizId == null) {
            log.warn("bizId cant be null. Please check your parameter. content:{}", content);
            return;
        }
        logPersistence.log(bizId, content);
    }

    public static <T, R> void log(Collection<T> bizIds, R content) {
        logPersistence.log(bizIds, content);
    }

    public static void log(LogRecord record) {
        logPersistence.log(record);
    }

    public static void log(Collection<LogRecord> records) {
        logPersistence.log(records);
    }
}