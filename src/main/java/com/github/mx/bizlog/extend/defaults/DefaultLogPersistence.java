package com.github.mx.bizlog.extend.defaults;

import com.alibaba.fastjson.JSON;
import com.github.mx.bizlog.bean.LogRecord;
import com.github.mx.bizlog.extend.LogPersistence;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Create by max on 2021/02/27
 **/
@Slf4j
public class DefaultLogPersistence implements LogPersistence, Runnable, AutoCloseable {

    private final ScheduledExecutorService executor;
    private static List<LogRecord> CURRENT = Lists.newCopyOnWriteArrayList();

    public DefaultLogPersistence() {
        ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat("biz-log-%s").build();
        executor = Executors.newSingleThreadScheduledExecutor(tf);
        executor.scheduleAtFixedRate(this, 10, 5, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
    }

    @Override
    public void log(LogRecord record) {
        CURRENT.add(record);
    }

    @Override
    public void log(Collection<LogRecord> record) {
        CURRENT.addAll(record);
    }

    @Override
    public <T, R> void log(T bizId, R content) {
        LogRecord record = new LogRecord(String.valueOf(bizId), JSON.toJSONString(content));
        CURRENT.add(record);
    }

    @Override
    public <T, R> void log(Collection<T> bizIds, R content) {
        List<LogRecord> records = Lists.newArrayListWithCapacity(bizIds.size());
        bizIds.forEach(bizId -> records.add(new LogRecord(String.valueOf(bizId), JSON.toJSONString(content))));
        CURRENT.addAll(records);
    }

    @Override
    public void close() throws Exception {
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("{} was interrupted. now exit.", Thread.currentThread().getName(), e);
        }
    }

    @Override
    public void run() {
        try {
            List<LogRecord> records = getAndSet();
            // 具体处理逻辑
            log.info("persist bizLog. {}", records);
        } catch (Exception e) {
            log.error("");
        }
    }

    private List<LogRecord> getAndSet() {
        List<LogRecord> old = CURRENT;
        CURRENT = Lists.newCopyOnWriteArrayList();
        return old;
    }
}