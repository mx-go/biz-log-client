package com.github.mx.bizlog.extend;

/**
 * 在表达式中使用自定义函数实现该接口
 * <p>
 * Create by max on 2021/02/27
 **/
public interface ParseFunction {

    /**
     * 获取自定义函数名称
     *
     * @return 自定义函数名称
     */
    String functionName();

    /**
     * 执行方法
     *
     * @param value 参数
     * @return 处理后的返回值
     */
    String apply(String value);
}
