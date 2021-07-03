package com.github.mx.bizlog.annotation;

import com.github.mx.bizlog.LogConfigureSelector;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * Annotate to the startup class
 * <p>
 * Create by max on 2021/02/27
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogConfigureSelector.class)
public @interface EnableLog {

    /**
     * 日志AOP的顺序
     */
    int order() default Ordered.LOWEST_PRECEDENCE;

    /**
     * Indicate how caching advice should be applied. The default is
     * {@link AdviceMode#PROXY}.
     *
     * @see AdviceMode
     */
    AdviceMode mode() default AdviceMode.PROXY;
}
