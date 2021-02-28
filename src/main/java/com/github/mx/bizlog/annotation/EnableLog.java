package com.github.mx.bizlog.annotation;

import com.github.mx.bizlog.LogConfigureSelector;
import com.github.mx.profiler.annotation.EnableProfiler;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解到启动类上
 * <p>
 * Create by max on 2021/02/27
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableProfiler
@Import(LogConfigureSelector.class)
public @interface EnableLog {

    /**
     * AspectJ pointcut expression
     * e.g：(execution(* com.mx.log..*.*(..)))
     */
    @AliasFor(annotation = EnableProfiler.class)
    String expression();

    /**
     * Indicate how caching advice should be applied. The default is
     * {@link AdviceMode#PROXY}.
     *
     * @see AdviceMode
     */
    @AliasFor(annotation = EnableProfiler.class)
    AdviceMode mode() default AdviceMode.PROXY;
}
