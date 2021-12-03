package com.github.mx.bizlog.parser;

import com.github.mx.bizlog.extend.defaults.ParseFunctionFactory;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析需要存储的日志里面的SpeEL表达式
 * <p>
 * Create by max on 2021/02/27
 **/
public class LogValueParser implements BeanFactoryAware {

    protected BeanFactory beanFactory;
    private LogExpressionEvaluator expressionEvaluator = new LogExpressionEvaluator();
    private static final Pattern PATTERN = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public Map<String, String> processTemplate(Collection<String> templates, Object ret, Class<?> targetClass, Method method, Object[] args, String errorMsg) {
        Map<String, String> expressionValues = Maps.newHashMap();
        EvaluationContext evaluationContext = expressionEvaluator.createEvaluationContext(method, args, targetClass, ret, errorMsg, beanFactory);

        for (String expressionTemplate : templates) {
            // 错误信息单独处理
            if (expressionTemplate.contains("#".concat(LogEvaluationContext.ERROR_MSG_VARIABLE_NAME))) {
                expressionValues.put(expressionTemplate, errorMsg);
                continue;
            }
            if (expressionTemplate.contains("{{") || expressionTemplate.contains("{")) {
                Matcher matcher = PATTERN.matcher(expressionTemplate);
                StringBuffer parsedStr = new StringBuffer();
                while (matcher.find()) {
                    String expression = matcher.group(2);
                    AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
                    String value = expressionEvaluator.parseExpression(expression, annotatedElementKey, evaluationContext);
                    String functionName = matcher.group(1);
                    matcher.appendReplacement(parsedStr, Matcher.quoteReplacement(Strings.nullToEmpty(ParseFunctionFactory.apply(functionName, value))));
                }
                matcher.appendTail(parsedStr);
                expressionValues.put(expressionTemplate, parsedStr.toString());
            } else {
                expressionValues.put(expressionTemplate, expressionTemplate);
            }
        }
        return expressionValues;
    }
}
