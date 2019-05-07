package com.qinfengsa.springframework.aop.intercept;

/**
 * Method拦截器（切面方法）
 * @author: qinfengsa
 * @date: 2019/5/5 19:25
 */
public interface GPMethodInterceptor {

    /**
     * 拦截方法 通过invoke执行
     * @param invocation
     * @return
     * @throws Throwable
     */
    Object invoke(GPMethodInvocation invocation) throws Throwable;
}
