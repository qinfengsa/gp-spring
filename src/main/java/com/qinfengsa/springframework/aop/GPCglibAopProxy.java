package com.qinfengsa.springframework.aop;

import com.qinfengsa.springframework.aop.intercept.GPMethodInvocation;
import com.qinfengsa.springframework.aop.support.GPAdvisedSupport;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * Gglib aop动态代理
 * @author: qinfengsa
 * @date: 2019/4/27 23:53
 */
public class GPCglibAopProxy implements GPAopProxy, MethodInterceptor {

    /**
     * 切面规则
     */
    private GPAdvisedSupport config;

    /**
     * 构造方法
     * @param config
     */
    public GPCglibAopProxy(GPAdvisedSupport config){
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(null);
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {

        //相当于Proxy，代理的工具类
        Enhancer enhancer = new Enhancer();
        if (Objects.nonNull(classLoader)) {
            enhancer.setClassLoader(classLoader);
        }
        // 生成一个继承clazz的子类
        enhancer.setSuperclass(this.config.getTargetClass());
        // 回调父类的方法
        enhancer.setCallback(this);
        return enhancer.create();
    }


    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        List<Object> interceptorsAndDynamicMethodMatchers = this.config.getInterceptorsAndDynamicInterceptionAdvice(method,
                this.config.getTargetClass());
        GPMethodInvocation invocation = new GPMethodInvocation(o,method,
                this.config.getTarget(),objects,this.config.getTargetClass(),interceptorsAndDynamicMethodMatchers);
        return invocation.proceed();

    }
}
