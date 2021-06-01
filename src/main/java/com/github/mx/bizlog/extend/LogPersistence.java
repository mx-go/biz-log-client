package com.github.mx.bizlog.extend;

import com.github.mx.bizlog.bean.LogRecord;

import java.util.Collection;

/**
 * 实现该接口持久化日志
 * 注意：
 * 如果使用数据库保存日志如需事务则需要新开事务，日志不能因为事务回滚而丢失。
 * 使用 @Transactional(propagation = Propagation.REQUIRES_NEW)
 * <p>
 * Create by max on 2021/02/27
 **/
public interface LogPersistence {

    void log(LogRecord record);

    <T, R> void log(T bizId, R content);

    <T, R> void log(Collection<T> bizIds, R content);
}