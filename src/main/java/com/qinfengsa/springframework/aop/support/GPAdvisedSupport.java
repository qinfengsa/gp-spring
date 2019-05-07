package com.qinfengsa.springframework.aop.support;

import com.qinfengsa.springframework.aop.aspect.GPAfterReturningAdviceInterceptor;
import com.qinfengsa.springframework.aop.aspect.GPAfterThrowingAdviceInterceptor;
import com.qinfengsa.springframework.aop.aspect.GPMethodBeforeAdviceInterceptor;
import com.qinfengsa.springframework.aop.config.GPAopConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 判断是否满足切面规则
 * @author: qinfengsa
 * @date: 2019/5/4 22:35
 */
@Slf4j
public class GPAdvisedSupport {

    /**
     * 功能增强的切面目标Class
     */
    private Class<?> targetClass;


    /**
     * 功能增强的切面实例 是代理对象
     * 这个实例会执行功能增城，如打印日志，事务
     */
    private Object target;


    /**
     * AOP配置属性
     */
    private GPAopConfig config;

    /**
     * 正则匹配，哪些类需要拦截并进行切面
     */
    private Pattern pointCutClassPattern;

    /**
     * 方法+参数缓存
     */
    private transient Map<Method, List<Object>> methodCache;

    /**
     * 构造方法 把配置文件注入进来
     * @param config
     */
    public GPAdvisedSupport(GPAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }



    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }


    /**
     * 获取
     * @param method
     * @param targetClass
     * @return
     * @throws Exception
     */
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception{
        List<Object> cached = methodCache.get(method);
        if(Objects.isNull(cached)){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());

            cached = methodCache.get(m);

            //底层逻辑，对代理方法进行一个兼容处理
            this.methodCache.put(m,cached);
        }

        return cached;
    }

    /**
     * 切面规则
     */
    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        //pointCut=public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)
        // 正则
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));

        try {

            methodCache = new HashMap<Method, List<Object>>();
            Pattern pattern = Pattern.compile(pointCut);
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String,Method>();
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(),m);
            }

            for (Method m : this.targetClass.getMethods()) {
                String methodString = m.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    // 执行器链 一个方法可能会被多个切面方法拦截（before,after）
                    List<Object> advices = new LinkedList<Object>();
                    // 把每一个方法包装成 MethodIterceptor
                    // before
                    if(StringUtils.isNotBlank(config.getAspectBefore())) {
                        // 创建一个Advivce
                        advices.add(new GPMethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()),aspectClass.newInstance()));
                    }
                    // after
                    if(StringUtils.isNotBlank(config.getAspectAfter())) {
                        // 创建一个Advivce
                        advices.add(new GPAfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
                    }
                    // afterThrowing
                    if(StringUtils.isNotBlank(config.getAspectAfterThrow())) {
                        //创建一个Advivce
                        GPAfterThrowingAdviceInterceptor throwingAdvice =
                                new GPAfterThrowingAdviceInterceptor(
                                        aspectMethods.get(config.getAspectAfterThrow()),
                                        aspectClass.newInstance());
                        throwingAdvice.setThrowingName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }

                    methodCache.put(m,advices);
                }

            }
        } catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    /**
     * 是否满足切面的规则
     * @return
     */
    public boolean pointCutMatch() {

        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
