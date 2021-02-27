package com.github.mx.bizlog.extend;

/**
 * Create by max on 2021/02/27
 **/
public interface LogOperator {

    /**
     * 可以在里面外部的获取当前登陆的用户，比如UserContext.getCurrentUser()
     *
     * @return 转换成Operator返回
     */
    String getOperatorId();
}
