package com.qinfengsa.springframework.aop.aspect;

import com.qinfengsa.springframework.aop.intercept.GPMethodInterceptor;
import com.qinfengsa.springframework.aop.intercept.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * 方法执行前通知
 * @author: qinfengsa
 * @date: 2019/5/5 19:31
 */
public class GPMethodBeforeAdviceInterceptor extends AbstractGPAspectJAdvice implements GPAdvice, GPMethodInterceptor {

    /**
     * 切面点
     */
    private GPJoinPoint joinPoint;

    /**
     * 构造传入
     * @param aspectMethod
     * @param aspectTarget
     */
    public GPMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    /**
     * 在invoke执行前调用
     * @param method
     * @param args
     * @param target
     * @throws Throwable
     */
    private void before(Method method,Object[] args,Object target) throws Throwable{
        // 把参数传入
        super.invokeAdviceMethod(this.joinPoint,null,null);

    }

    /**
     * 切面方法
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(GPMethodInvocation invocation) throws Throwable {
        this.joinPoint = invocation;
        before(invocation.getMethod(),invocation.getArguments(),invocation.getThis());
        // 切面方法调用完之后调用下一个拦截代理或者原本方法
        return invocation.proceed();
    }
}
