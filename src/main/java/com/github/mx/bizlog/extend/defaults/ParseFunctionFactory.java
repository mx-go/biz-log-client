package com.github.mx.bizlog.extend.defaults;

import com.github.mx.bizlog.extend.ParseFunction;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Create by max on 2021/02/27
 **/
public class ParseFunctionFactory {

    private static Map<String, ParseFunction> functionMap;

    public ParseFunctionFactory(List<ParseFunction> parseFunctions) {
        functionMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(parseFunctions)) {
            return;
        }
        for (ParseFunction function : parseFunctions) {
            if (StringUtils.isEmpty(function.functionName())) {
                continue;
            }
            functionMap.put(function.functionName(), function);
        }
    }

    public static String apply(String functionName, String value) {
        ParseFunction function = getFunction(functionName);
        if (function == null) {
            return value;
        }
        return function.apply(value);
    }

    private static ParseFunction getFunction(String functionName) {
        return functionMap.get(functionName);
    }
}
