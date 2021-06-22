package com.github.mx.bizlog;

import com.github.mx.bizlog.annotation.EnableLog;
import com.github.mx.bizlog.aop.BeanFactoryLogAdvisor;
import com.github.mx.bizlog.aop.LogInterceptor;
import com.github.mx.bizlog.aop.LogOperationSource;
import com.github.mx.bizlog.extend.LogOperator;
import com.github.mx.bizlog.extend.LogPersistence;
import com.github.mx.bizlog.extend.ParseFunction;
import com.github.mx.bizlog.extend.defaults.DefaultLogOperator;
import com.github.mx.bizlog.extend.defaults.DefaultLogPersistence;
import com.github.mx.bizlog.extend.defaults.DefaultParseFunction;
import com.github.mx.bizlog.extend.defaults.ParseFunctionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

/**
 * Create by max on 2021/02/27
 **/
@Configuration
@Slf4j
public class LogProxyAutoConfiguration implements ImportAware {

    private AnnotationAttributes enableLogAnnotation;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        enableLogAnnotation = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableLog.class.getName(), false));
        if (enableLogAnnotation == null) {
            log.info("@EnableLog is not present on importing class");
        }
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogOperationSource logOperationSource() {
        return new LogOperationSource();
    }

    @Bean
    public ParseFunctionFactory parseFunctionFactory(@Autowired List<ParseFunction> parseFunctions) {
        return new ParseFunctionFactory(parseFunctions);
    }

    @Bean
    @ConditionalOnMissingBean(ParseFunction.class)
    public DefaultParseFunction parseFunction() {
        return new DefaultParseFunction();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryLogAdvisor logAdvisor() {
        BeanFactoryLogAdvisor advisor = new BeanFactoryLogAdvisor();
        advisor.setLogOperationSource(logOperationSource());
        advisor.setAdvice(logInterceptor());
        advisor.setOrder(enableLogAnnotation.getNumber("order"));
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public LogInterceptor logInterceptor() {
        LogInterceptor interceptor = new LogInterceptor();
        interceptor.setLogOperationSource(logOperationSource());
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(LogOperator.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public LogOperator logOperator() {
        return new DefaultLogOperator();
    }

    @Bean
    @ConditionalOnMissingBean(LogPersistence.class)
    @Role(BeanDefinition.ROLE_APPLICATION)
    public LogPersistence logPersistence() {
        return new DefaultLogPersistence();
    }
}
