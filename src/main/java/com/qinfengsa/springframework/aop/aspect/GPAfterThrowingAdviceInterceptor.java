package com.qinfengsa.springframework.aop.aspect;

import com.qinfengsa.springframework.aop.intercept.GPMethodInterceptor;
import com.qinfengsa.springframework.aop.intercept.GPMethodInvocation;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 方法抛出异常后执行通知
 * @author: qinfengsa
 * @date: 2019/5/5 19:31
 */
@Slf4j
public class GPAfterThrowingAdviceInterceptor extends GPAbstractAspectJAdvice implements GPAdvice, GPMethodInterceptor {



    /**
     * 异常名称
     */
    private String throwingName;

    /**
     * 构造传入
     *
     * @param aspectMethod
     * @param aspectTarget
     */
    public GPAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(GPMethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Throwable e) {
            invokeAdviceMethod(invocation,null,e.getCause());
            throw e;
        }
    }

    /**
     * 设置异常名称
     * @param throwingName
     */
    public void setThrowingName(String throwingName) {
        this.throwingName = throwingName;
    }

}
