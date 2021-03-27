package com.github.mx.bizlog.context;

import java.util.Map;

/**
 * Create by max on 2021/02/27
 **/
public class LogRecordContext {

    private static final InheritableThreadLocal<Map<String, Object>> variableMap = new InheritableThreadLocal<>();

    public static void putVariable(String name, Object value) {
        variableMap.get().put(name, value);
    }

    public static Map<String, Object> getVariables() {
        return variableMap.get();
    }

    public static void clear() {
        variableMap.get().clear();
    }
}