package com.github.mx.bizlog.annotation;

import com.github.mx.bizlog.LogConfigureSelector;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;

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
@Import(LogConfigureSelector.class)
public @interface EnableLog {

    /**
     * AspectJ pointcut expression
     * e.g：(execution(* com.mx.log..*.*(..)))
     */
    String expression();

    /**
     * Indicate how caching advice should be applied. The default is
     * {@link AdviceMode#PROXY}.
     *
     * @see AdviceMode
     */
    AdviceMode mode() default AdviceMode.PROXY;
}
