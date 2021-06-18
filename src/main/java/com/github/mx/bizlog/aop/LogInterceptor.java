package com.github.mx.bizlog.aop;

import com.github.mx.bizlog.bean.LogOps;
import com.github.mx.bizlog.bean.LogRecord;
import com.github.mx.bizlog.context.BizLog;
import com.github.mx.bizlog.context.LogContext;
import com.github.mx.bizlog.extend.LogOperator;
import com.github.mx.bizlog.extend.LogPersistence;
import com.github.mx.bizlog.extend.defaults.DefaultLogOperator;
import com.github.mx.bizlog.parser.LogValueParser;
import com.github.mx.nacos.config.core.ConfigFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Collection;
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
        BizLog.setLogPersistence(logPersistence);
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
        LocalDateTime startTime = LocalDateTime.now();
        try {
            LogContext.putEmptySpan();
            ret = invoker.proceed();
        } catch (Exception e) {
            success = false;
            errorMsg = ExceptionUtils.getStackTrace(e);
            throwable = e;
        } finally {
            LocalDateTime endTime = LocalDateTime.now();
            // 处理及记录日志
            processLog(method, args, targetClass, ret, success, errorMsg, startTime, endTime);
            LogContext.clear();
        }
        if (throwable != null) {
            throw throwable;
        }
        return ret;
    }

    private Class<?> getTargetClass(Object target) {
        return AopProxyUtils.ultimateTargetClass(target);
    }

    private void processLog(Method method, Object[] args, Class<?> targetClass, Object ret, boolean success,
                            String errorMsg, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            Collection<LogOps> operations = getLogOperationSource().computeLogOperations(method, targetClass);
            if (!CollectionUtils.isEmpty(operations)) {
                recordLog(ret, method, args, operations, targetClass, success, errorMsg, startTime, endTime);
            }
        } catch (Exception e) {
            //记录日志错误不要影响业务
            log.error("process log record parse exception", e);
        }
    }

    private void recordLog(Object ret, Method method, Object[] args, Collection<LogOps> operations, Class<?> targetClass,
                           boolean success, String errorMsg, LocalDateTime startTime, LocalDateTime endTime) {
        for (LogOps logOps : operations) {
            try {
                String action = success ? logOps.getSuccessLogTemplate() : logOps.getFailLogTemplate();
                if (!StringUtils.isEmpty(logOps.getLogType())) {
                    LogRecord logRecord = this.wrapper(ret, method, args, targetClass, success, errorMsg, logOps, action, startTime, endTime);
                    Preconditions.checkNotNull(logPersistence, "logPersistence not init!!");
                    this.logPersistence.log(logRecord);
                }
            } catch (Exception e) {
                log.error("log record execute exception", e);
            }
        }
    }

    private LogRecord wrapper(Object ret, Method method, Object[] args, Class<?> targetClass, boolean success,
                              String errorMsg, LogOps logOps, String action, LocalDateTime startTime, LocalDateTime endTime) {
        String bizId = logOps.getBizId();
        String logType = logOps.getLogType();
        String operatorId = logOps.getOperatorId();
        String category = logOps.getCategory();
        String content = logOps.getContent();
        //获取需要解析的表达式
        List<String> spElTemplates;
        String realOperator = "";
        if (StringUtils.isEmpty(operatorId) || DefaultLogOperator.DEFAULT_OPERATOR_ID.equalsIgnoreCase(operatorId)) {
            spElTemplates = Lists.newArrayList(bizId, action, category, content);
            if (StringUtils.isEmpty(logOperator.getOperatorId())) {
                log.warn("operatorId is null");
            }
            realOperator = logOperator.getOperatorId();
        } else {
            spElTemplates = Lists.newArrayList(bizId, action, category, content, operatorId);
        }
        Map<String, String> expressionValues = processTemplate(spElTemplates, ret, targetClass, method, args, errorMsg);

        return LogRecord.builder()
                .appName(ConfigFactory.getApplicationName())
                .logType(logType)
                .bizId(expressionValues.get(bizId))
                .operatorId(!StringUtils.isEmpty(realOperator) ? realOperator : expressionValues.get(operatorId))
                .category(expressionValues.get(category))
                .content(expressionValues.get(content))
                .success(success)
                .action(expressionValues.get(action))
                .startTime(startTime)
                .endTime(endTime)
                .createTime(LocalDateTime.now())
                .build();
    }
}