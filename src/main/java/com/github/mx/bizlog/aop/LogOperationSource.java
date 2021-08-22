package com.github.mx.bizlog.aop;

import com.github.mx.bizlog.annotation.Log;
import com.github.mx.bizlog.bean.LogOps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * 处理注解参数
 * <p>
 * Create by max on 2021/02/27
 **/
public class LogOperationSource {

    public Collection<LogOps> computeLogOperations(Method method, Class<?> targetClass) {
        // Don't allow no-public methods as required.
        if (!Modifier.isPublic(method.getModifiers())) {
            return Collections.emptyList();
        }

        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        // First try is the method in the target class.
        return parseLogAnnotations(specificMethod);
    }

    private Collection<LogOps> parseLogAnnotations(AnnotatedElement ae) {
        Collection<Log> logAnnotations = AnnotatedElementUtils.getAllMergedAnnotations(ae, Log.class);
        Collection<LogOps> ret = null;
        if (!logAnnotations.isEmpty()) {
            ret = new ArrayList<>(1);
            for (Log annotation : logAnnotations) {
                ret.add(parseLogAnnotation(ae, annotation));
            }
        }
        return ret;
    }

    private LogOps parseLogAnnotation(AnnotatedElement ae, Log logAnnotation) {
        LogOps logOps = LogOps.builder()
                .title(logAnnotation.title())
                .successLogTemplate(logAnnotation.success())
                .failLogTemplate(logAnnotation.fail())
                .detail(logAnnotation.detail())
                .logType(logAnnotation.logType())
                .bizId(logAnnotation.bizId())
                .operatorId(logAnnotation.operatorId())
                .category(logAnnotation.category())
                .content(logAnnotation.content())
                .build();
        validateLogOperation(ae, logOps);
        return logOps;
    }

    private void validateLogOperation(AnnotatedElement ae, LogOps logOps) {
        if (StringUtils.isAnyBlank(logOps.getBizId())) {
            throw new IllegalStateException("Invalid log annotation configuration on '" +
                    ae.toString() + "'. 'bizId' attribute must be set.");
        }
    }
}