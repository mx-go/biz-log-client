package com.github.mx.bizlog.aop;

import com.github.mx.bizlog.bean.LogOps;
import com.github.mx.bizlog.bean.LogRecord;
import com.github.mx.bizlog.context.LogRecordContext;
import com.github.mx.bizlog.extend.LogOperator;
import com.github.mx.bizlog.extend.LogPersistence;
import com.github.mx.bizlog.parser.LogValueParser;
import com.github.mx.nacos.config.core.ConfigFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Create by max on 2021/02/27
 **/
@Slf4j
public class LogInterceptor extends LogValueParser implements InitializingBean, MethodInterceptor, Serializable {

    private static final long serialVersionUID = -8123862383945755569L;

    @Getter
    @Setter
    private transient LogOperationSource logOperationSource;
    private transient LogPersistence logPersistence;
    private transient LogOperator logOperator;

    @Override
    public void afterPropertiesSet() {
        logPersistence = beanFactory.getBean(LogPersistence.class);
        logOperator = beanFactory.getBean(LogOperator.class);
        Preconditions.checkNotNull(logPersistence, "logPersistence not null");
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        return execute(invocation, invocation.getThis(), method, invocation.getArguments());
    }

    private Object execute(MethodInvocation invoker, Object target, Method method, Object[] args) throws Throwable {
        Class<?> targetClass = getTargetClass(target);
        Object ret = null;
        boolean success = true;
        String errorMsg = "";
        Throwable throwable = null;
        try {
            LogRecordContext.clear();
            ret = invoker.proceed();
        } catch (Exception e) {
            success = false;
            errorMsg = e.getClass().getName();
            throwable = e;
        } finally {
            // 处理及记录日志
            processLog(method, args, targetClass, ret, success, errorMsg);
            LogRecordContext.clear();
        }
        if (throwable != null) {
            throw throwable;
        }
        return ret;
    }

    private Class<?> getTargetClass(Object target) {
        return AopProxyUtils.ultimateTargetClass(target);
    }

    private void processLog(Method method, Object[] args, Class<?> targetClass, Object ret, boolean success, String errorMsg) {
        try {
            Collection<LogOps> operations = getLogOperationSource().computeLogOperations(method, targetClass);
            if (!CollectionUtils.isEmpty(operations)) {
                recordLog(ret, method, args, operations, targetClass, success, errorMsg);
            }
        } catch (Exception e) {
            //记录日志错误不要影响业务
            log.error("process log record parse exception", e);
        }
    }

    private void recordLog(Object ret, Method method, Object[] args, Collection<LogOps> operations,
                           Class<?> targetClass, boolean success, String errorMsg) {
        for (LogOps logOps : operations) {
            try {
                String action = success ? logOps.getSuccessLogTemplate() : logOps.getFailLogTemplate();
                if (success || !StringUtils.isEmpty(action)) {
                    LogRecord logRecord = this.wrapper(ret, method, args, targetClass, success, errorMsg, logOps, action);
                    Preconditions.checkNotNull(logPersistence, "logPersistence not init!!");
                    this.logPersistence.persist(logRecord);
                }
            } catch (Exception e) {
                log.error("log record execute exception", e);
            }
        }
    }

    private LogRecord wrapper(Object ret, Method method, Object[] args, Class<?> targetClass, boolean success,
                              String errorMsg, LogOps logOps, String action) {
        String bizId = logOps.getBizId();
        String operatorId = logOps.getOperatorId();
        String category = logOps.getCategory();
        String content = logOps.getContent();
        //获取需要解析的表达式
        List<String> spElTemplates;
        String realOperator = "";
        if (StringUtils.isEmpty(operatorId)) {
            spElTemplates = Lists.newArrayList(bizId, action, category, content);
            if (StringUtils.isEmpty(logOperator.getOperatorId())) {
                throw new IllegalArgumentException("operatorId is null");
            }
            realOperator = logOperator.getOperatorId();
        } else {
            spElTemplates = Lists.newArrayList(bizId, action, category, content, operatorId);
        }
        Map<String, String> expressionValues = processTemplate(spElTemplates, ret, targetClass, method, args, errorMsg);

        return LogRecord.builder()
                .appName(ConfigFactory.getApplicationName())
                .bizId(expressionValues.get(bizId))
                .operatorId(!StringUtils.isEmpty(realOperator) ? realOperator : expressionValues.get(operatorId))
                .category(expressionValues.get(category))
                .content(expressionValues.get(content))
                .success(success)
                .action(expressionValues.get(action))
                .createTime(new Date())
                .build();
    }
}