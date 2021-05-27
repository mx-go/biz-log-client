package com.github.mx.bizlog.context;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 自定义变量上下文
 * <p>
 * Create by max on 2021/02/27
 **/
public class LogRecordContext {

    private static final InheritableThreadLocal<Map<String, Object>> VARIABLES = new InheritableThreadLocal<>();

    public static void putVariable(String name, Object value) {
        if (VARIABLES.get() == null) {
            HashMap<String, Object> map = Maps.newHashMap();
            map.put(name, value);
            VARIABLES.set(map);
        }
        VARIABLES.get().put(name, value);
    }

    public static Map<String, Object> getVariables() {
        return Optional.ofNullable(VARIABLES.get()).orElse(Maps.newHashMap());
    }

    public static void clear() {
        if (VARIABLES.get() != null) {
            VARIABLES.get().clear();
        }
    }
}