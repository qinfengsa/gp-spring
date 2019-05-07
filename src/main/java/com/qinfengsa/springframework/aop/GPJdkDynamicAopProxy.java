package com.qinfengsa.springframework.aop;

import com.qinfengsa.springframework.aop.intercept.GPMethodInvocation;
import com.qinfengsa.springframework.aop.support.GPAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * JDK动态代理
 * @author: qinfengsa
 * @date: 2019/4/27 23:55
 */
public class GPJdkDynamicAopProxy implements GPAopProxy, InvocationHandler {

    /**
     * 切面规则
     */
    private GPAdvisedSupport config;

    /**
     * 构造方法
     * @param config
     */
    public GPJdkDynamicAopProxy(GPAdvisedSupport config){
        this.config = config;
    }


    /**
     * 获取代理对象
     * @return
     */
    @Override
    public Object getProxy() {
        return getProxy(this.config.getTargetClass().getClassLoader());
    }

    /**
     * 获取代理对象，传入类加载器
     * @param classLoader
     * @return
     */
    @Override
    public Object getProxy(ClassLoader classLoader) {

        return Proxy.newProxyInstance(classLoader,this.config.getTargetClass().getInterfaces(),this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        List<Object> interceptorsAndDynamicMethodMatchers = this.config.getInterceptorsAndDynamicInterceptionAdvice(method,
                this.config.getTargetClass());
        GPMethodInvocation invocation = new GPMethodInvocation(proxy,method,
                this.config.getTarget(),args,this.config.getTargetClass(),interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();
    }
}


