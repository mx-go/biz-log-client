package com.github.mx.bizlog.aop;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Create by max on 2021/02/27
 **/
@SuppressWarnings("NullableProblems")
public class LogPointcut extends StaticMethodMatcherPointcut implements Serializable {

    private static final long serialVersionUID = 2710376192529289594L;
    private transient LogOperationSource logOperationSource;

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return !CollectionUtils.isEmpty(logOperationSource.computeLogOperations(method, targetClass));
    }

    void setLogOperationSource(LogOperationSource logOperationSource) {
        this.logOperationSource = logOperationSource;
    }
}