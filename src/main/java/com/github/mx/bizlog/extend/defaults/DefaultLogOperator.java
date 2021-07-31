package com.github.mx.bizlog.extend.defaults;

import com.github.mx.bizlog.extend.LogOperator;

/**
 * Create by max on 2021/02/27
 **/
public class DefaultLogOperator implements LogOperator {

    public static final String DEFAULT_OPERATOR_ID = "system";

    @Override
    public String getOperatorId() {
        return DEFAULT_OPERATOR_ID;
    }
}