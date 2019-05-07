package com.qinfengsa.springframework.aop.aspect;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 抽象通知类型
 * @author: qinfengsa
 * @date: 2019/5/7 06:04
 */
public abstract class GPAbstractAspectJAdvice implements GPAdvice {

    /**
     * 切面方法
     */
    private Method aspectMethod;

    /**
     * 切面对象
     */
    private Object aspectTarget;

    /**
     * 构造传入
     * @param aspectMethod
     * @param aspectTarget
     */
    public GPAbstractAspectJAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    /**
     * 调用通知方法
     * @param joinPoint
     * @param returnValue
     * @param tx
     * @return
     * @throws Throwable
     */
    public Object invokeAdviceMethod(GPJoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable{
        // 拿到参数
        Class<?> [] paramTypes = this.aspectMethod.getParameterTypes();
        // 如果参数为空，直接调用
        if (Objects.isNull(paramTypes) || paramTypes.length == 0) {
            return this.aspectMethod.invoke(this.aspectTarget);
        } else {
            // 给参数赋值
            Object [] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i ++) {
                if (paramTypes[i] == GPJoinPoint.class){
                    args[i] = joinPoint;
                } else if(paramTypes[i] == Throwable.class){
                    args[i] = tx;
                } else if(paramTypes[i] == Object.class){
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget,args);
        }

    }
}
