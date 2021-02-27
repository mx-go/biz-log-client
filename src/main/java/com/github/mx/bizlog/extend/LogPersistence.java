package com.github.mx.bizlog.extend;

import com.github.mx.bizlog.bean.LogRecord;

/**
 * 实现该接口持久化日志
 * 注意：
 * 保存日志如需事务这需要新开事务，日志不能因为事务回滚而丢失。
 * 使用 @Transactional(propagation = Propagation.REQUIRES_NEW)
 * <p>
 * Create by max on 2021/02/27
 **/
public interface LogPersistence {

    /**
     * 持久化log
     */
    void persist(LogRecord record);
}
