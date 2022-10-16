package com.github.mx.bizlog.parser;

import com.alibaba.fastjson.serializer.ValueFilter;

import java.util.Objects;

/**
 * 处理Long类型字段，避免前端展示时精度丢失
 * <p>
 * Create by max on 2022/10/16
 **/
public class CustomValueFilter implements ValueFilter {

    /**
     * 超过多少位转换为String类型
     */
    private static final int THRESHOLD_LENGTH = 16;

    @Override
    public Object process(Object object, String name, Object value) {
        if (Objects.nonNull(value) && value instanceof Long && value.toString().length() >= THRESHOLD_LENGTH) {
            return String.valueOf(value);
        }
        return value;
    }
}
