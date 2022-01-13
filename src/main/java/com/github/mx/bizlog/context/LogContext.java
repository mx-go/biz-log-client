package com.github.mx.bizlog.context;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 自定义变量上下文
 * <p>
 * Create by max on 2021/02/27
 **/
public class LogContext {

    private static final InheritableThreadLocal<Stack<Map<String, Object>>> VARIABLE_MAP_STACK = new InheritableThreadLocal<>();

    public static void putVariable(String name, Object value) {
        if (VARIABLE_MAP_STACK.get() == null) {
            Stack<Map<String, Object>> stack = new Stack<>();
            VARIABLE_MAP_STACK.set(stack);
        }
        Stack<Map<String, Object>> mapStack = VARIABLE_MAP_STACK.get();
        if (mapStack.size() == 0) {
            VARIABLE_MAP_STACK.get().push(new HashMap<>());
        }
        VARIABLE_MAP_STACK.get().peek().put(name, value);
    }

    public static <T, R> void putVariable(String name, List<T> argument, Function<T, R> mapper) {
        putVariable(name, argument.stream().map(mapper).collect(Collectors.toList()));
    }

    public static Object getVariable(String key) {
        Map<String, Object> variableMap = VARIABLE_MAP_STACK.get().peek();
        return variableMap.get(key);
    }

    public static Map<String, Object> getVariables() {
        Stack<Map<String, Object>> mapStack = VARIABLE_MAP_STACK.get();
        return mapStack.peek();
    }

    public static void clear() {
        if (VARIABLE_MAP_STACK.get() != null) {
            VARIABLE_MAP_STACK.get().pop();
        }
    }

    /**
     * 日志使用方不需要使用到这个方法
     * 每进入一个方法初始化一个 span 放入到 stack中，方法执行完后 pop 掉这个span
     */
    public static void putEmptySpan() {
        Stack<Map<String, Object>> mapStack = VARIABLE_MAP_STACK.get();
        if (mapStack == null) {
            Stack<Map<String, Object>> stack = new Stack<>();
            VARIABLE_MAP_STACK.set(stack);
        }
        VARIABLE_MAP_STACK.get().push(Maps.newHashMap());
    }
}