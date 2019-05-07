package com.qinfengsa.springframework.aop.intercept;

import com.qinfengsa.springframework.aop.aspect.GPAdvice;
import com.qinfengsa.springframework.aop.aspect.GPJoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Method 调用器，负责调用切面方法,并最终调用原本的方法
 * @author: qinfengsa
 * @date: 2019/5/5 19:26
 */
public class GPMethodInvocation implements GPJoinPoint {

    /**
     * 代理对象
     */
    private Object proxy;

    /**
     * 代理方法
     */
    private Method method;

    /**
     * 目标对象
     */
    private Object target;

    /**
     * 参数集合
     */
    private Object [] arguments;

    /**
     * 执行器链
     */
    private List<Object> interceptorsAndDynamicMethodMatchers;

    /**
     * 目标类型
     */
    private Class<?> targetClass;

    /**
     * 索引,记录当前拦截器执行的位置 责任链模式
     */
    private int currentInterceptorIndex = -1;

    /**
     * 数据缓存：用于方法之间共享
     */
    private Map<String, Object> userAttributes;

    /**
     * 构造方法
     * @param proxy
     * @param method
     * @param target
     * @param arguments
     * @param interceptorsAndDynamicMethodMatchers
     * @param targetClass
     */
    public GPMethodInvocation(Object proxy, Method method, Object target, Object[] arguments,Class<?> targetClass,
        List<Object> interceptorsAndDynamicMethodMatchers  ) {
        this.proxy = proxy;
        this.method = method;
        this.target = target;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
        this.targetClass = targetClass;
    }


    /**
     * Method 调用
     * @return
     * @throws Throwable
     */
    public Object proceed() throws Throwable {

        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            // 切面拦截器链方法都执行完了，就轮到调用原本的方法
            return this.method.invoke(this.target,this.arguments);
        }
        // 否则调用下一个拦截通知方法（before,after,）
        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);

        if (interceptorOrInterceptionAdvice instanceof GPMethodInterceptor) {
            // 所以吸引定义MethodInterceptor接口
            GPMethodInterceptor mi = (GPMethodInterceptor) interceptorOrInterceptionAdvice;
            return  mi.invoke(this);
        } else {
            // 不是拦截器接口，在调用自身，直到调用链结束
            return proceed();
        }



    }


    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    /**
     * 设置缓存
     * @param key
     * @param value
     */
    @Override
    public void setUserAttribute(String key, Object value) {
        if (Objects.nonNull(value)) {
            if (Objects.isNull(this.userAttributes)) {
                this.userAttributes = new HashMap<>();
            }
            this.userAttributes.put(key,value);

        } else if (Objects.nonNull(this.userAttributes)) {
            this.userAttributes.remove(key);
        }

    }

    /**
     * 取出缓存
     * @param key
     * @return
     */
    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }
}
