package com.github.mx.bizlog.aop;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

/**
 * Create by max on 2021/02/27
 **/
public class BeanFactoryLogAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private static final long serialVersionUID = 6167069643755400474L;
    private final LogPointcut pointcut = new LogPointcut();

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    public void setLogOperationSource(LogOperationSource logOperationSource) {
        pointcut.setLogOperationSource(logOperationSource);
    }
}