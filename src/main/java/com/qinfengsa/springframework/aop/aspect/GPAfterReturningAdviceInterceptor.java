package com.qinfengsa.springframework.aop.aspect;

import com.qinfengsa.springframework.aop.intercept.GPMethodInterceptor;
import com.qinfengsa.springframework.aop.intercept.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * 方法结束后执行通知
 * @author: qinfengsa
 * @date: 2019/5/5 19:31
 */
public class GPAfterReturningAdviceInterceptor extends GPAbstractAspectJAdvice implements GPAdvice, GPMethodInterceptor {

    /**
     * 切面点
     */
    private GPJoinPoint joinPoint;

    /**
     * 构造传入
     * @param aspectMethod
     * @param aspectTarget
     */
    public GPAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    /**
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(GPMethodInvocation invocation) throws Throwable {
        // 切面方法调用之前调用下一个拦截代理或者原本方法
        Object result = invocation.proceed();
        this.joinPoint = invocation;
        afterReturning(result,invocation.getMethod(),invocation.getArguments(),invocation.getThis());
        return result;
    }

    /**
     * 在invoke执行后调用
     * @param retVal
     * @param method
     * @param arguments
     * @param aThis
     * @throws Throwable
     */
    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
