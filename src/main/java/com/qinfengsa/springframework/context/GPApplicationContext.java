package com.qinfengsa.springframework.context;

import com.qinfengsa.springframework.annotation.GPAutowired;
import com.qinfengsa.springframework.annotation.GPController;
import com.qinfengsa.springframework.annotation.GPService;
import com.qinfengsa.springframework.aop.GPAopProxy;
import com.qinfengsa.springframework.aop.GPCglibAopProxy;
import com.qinfengsa.springframework.aop.GPJdkDynamicAopProxy;
import com.qinfengsa.springframework.aop.config.GPAopConfig;
import com.qinfengsa.springframework.aop.support.GPAdvisedSupport;
import com.qinfengsa.springframework.beans.GPBeanWrapper;
import com.qinfengsa.springframework.beans.config.GPBeanDefinition;
import com.qinfengsa.springframework.beans.config.GPBeanPostProcessor;
import com.qinfengsa.springframework.beans.support.GPBeanDefinitionReader;
import com.qinfengsa.springframework.beans.support.GPDefaultListableBeanFactory;
import com.qinfengsa.springframework.core.GPBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IOC容器
 * @author: qinfengsa
 * @date: 2019/4/20 00:00
 */
@Slf4j
public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {

    /**
     * 配置文件
     */
    private String[] configLocations;


    /**
     * 配置文件加载器
     */
    private GPBeanDefinitionReader reader;


    /**
     * 单例的IOC容器缓存
     */
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();

    /**
     * IOC容器 className 做key, 考虑到可以通过别名注入
     */
    private Map<String,GPBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();


    /**
     * 通过配置文件构造IOC容器
     * @param configLocations
     */
    public GPApplicationContext(String... configLocations) {
        setConfigLocations(configLocations);
        try {
            refresh();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

    }

    /**
     * 核心方法refresh()，用模板模式定义了整个IOC流程
     * @throws Exception
     */
    private void refresh() throws Exception {
        // 1、定位，定位配置文件
        reader = new GPBeanDefinitionReader(this.configLocations);

        // 2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<GPBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        // 3、注册，把配置信息放到容器里面(伪IOC容器)
        doRegisterBeanDefinition(beanDefinitions);

        // 4、把不是延时加载的类，有提前初始化
        doAutowrited();


    }

    /**
     * 遍历beanDefinitionMap , 然后初始化非懒加载的bean
     */
    private void doAutowrited() {

        for (Map.Entry<String,GPBeanDefinition> entry : super.beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            if (entry.getValue().isLazyInit()) {
                continue;
            }
            try {
                //加载对应的Bean
                getBean(beanName);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }

        }

    }

    /**
     * 注册，把配置信息放到容器里面(伪IOC容器)
     * @param beanDefinitions
     */
    private void doRegisterBeanDefinition(List<GPBeanDefinition> beanDefinitions) throws Exception {

        for (GPBeanDefinition beanDefinition: beanDefinitions) {

            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The Bean '" + beanDefinition.getFactoryBeanName() + "' is already exists!!");
            }

            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }

    }

    /**
     * 读取配置文件的路径，并保存到this.configLocations(String数组)对象中
     * @param locations
     */
    private void setConfigLocations(String[] locations) {
        if (locations != null) {
            this.configLocations = new String[locations.length];
            for (int i = 0; i < locations.length; i++) {
                // 简单处理，把classpath替换掉，并去除首尾空格，Spring的处理很复杂
                this.configLocations[i] = locations[0].replace("classpath:","").trim();
            }
        }  else {
            this.configLocations = null;
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        GPBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);



        // 根据beanDefinition实例化对象
        Object instance = instantiateBean(beanName,beanDefinition);

        // 把对象封装到BeanWrapper中
        GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);

        // 拿到BeanWraoper之后，把BeanWrapper保存到IOC容器中去
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);


        // 注入
        populateBean(beanName,new GPBeanDefinition(),beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    /**
     * 注入
     * @param beanName
     * @param gpBeanDefinition
     * @param beanWrapper
     */
    private void populateBean(String beanName, GPBeanDefinition gpBeanDefinition, GPBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();


        Class<?> clazz = beanWrapper.getWrappedClass();
        // 判断只有加了注解的类，才执行依赖注入
        if(!(clazz.isAnnotationPresent(GPController.class) ||
                clazz.isAnnotationPresent(GPService.class))){
            return;
        }

        // 获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 有Autowired注解才进行注入
            if (!field.isAnnotationPresent(GPAutowired.class)){ continue;}

            GPAutowired autowired = field.getAnnotation(GPAutowired.class);

            String autowiredBeanName = autowired.value().trim();
            if (StringUtils.isBlank(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }

            // 强制访问
            field.setAccessible(true);

            try {
                // 为什么会为NULL，先留个坑
                // 可能会产生循环依赖，可能要注入的依赖还没有实例化
                if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){ continue; }

                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(),e);
            }

        }
    }

    /**
     * 通过beanDefinition 创建实例
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(String beanName, GPBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {

            //  容器中存在
            if (this.factoryBeanInstanceCache.containsKey(className)) {

                return this.factoryBeanInstanceCache.get(className).getWrappedInstance();
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                // 判断bean的Class是否满足切面规则
                GPAdvisedSupport config = instantionAopConfig(beanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);

                // 符合PointCut的规则的话，创建代理对象
                if (config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }

                this.factoryBeanObjectCache.put(className,instance);
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(),instance);

            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return instance;
    }

    /**
     * 从配置文件中获取配置信息，并记录到GPAopConfig
     * @param beanDefinition
     * @return
     */
    private GPAdvisedSupport instantionAopConfig(GPBeanDefinition beanDefinition) {
        GPAopConfig config = new GPAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));

        return  new GPAdvisedSupport(config);
    }

    /**
     * 创建代理对象
     * @param config
     * @return
     */
    private GPAopProxy createProxy(GPAdvisedSupport config) {

        Class targetClass = config.getTargetClass();
        // 如果有接口就创建JDK动态代理
        if (targetClass.getInterfaces().length > 0){
            return new GPJdkDynamicAopProxy(config);
        }

        return new GPCglibAopProxy(config);
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    public String[] getBeanDefinitionNames () {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public Properties getConfig() {

        return this.reader.getConfig();
    }

}
