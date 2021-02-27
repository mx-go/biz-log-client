package com.github.mx.bizlog.extend.defaults;

import com.github.mx.bizlog.bean.LogRecord;
import com.github.mx.bizlog.extend.LogPersistence;
import lombok.extern.slf4j.Slf4j;

/**
 * Create by max on 2021/02/27
 **/
@Slf4j
public class DefaultLogPersistence implements LogPersistence {

    @Override
    public void persist(LogRecord record) {
        log.info("persist bizLog. {}", record);
    }
}