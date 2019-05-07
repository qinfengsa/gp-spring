package com.qinfengsa.springframework.beans.support;

import com.qinfengsa.springframework.beans.config.GPBeanDefinition;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * 从配置文件中读取bean的信息
 * @author: qinfengsa
 * @date: 2019/4/21 23:51
 */
@Slf4j
public class GPBeanDefinitionReader {

    /**
     * 注册类的集合
     */
    private List<String> registyBeanClasses = new ArrayList<String>();


    /**
     * 配置信息
     */
    private Properties config = new Properties();

    /**
     * 固定配置文件中的key，相对于xml的规范
     */ 
    private final String SCAN_PACKAGE = "scanPackage";

    /**
     * 构造方法，简析配置文件，然后把所有类记录到一个List里
     * @param configLocations
     */
    public GPBeanDefinitionReader(String[] configLocations) {
        for (String configLocation : configLocations) {

            InputStream is = this.getClass().getClassLoader().getResourceAsStream(configLocation);

            try {
                config.load(is);
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            } finally {

                if (Objects.nonNull(is)) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(),e);
                    }
                }
            }
        }
        // 扫描相关的类
        doScanner(this.config.getProperty(SCAN_PACKAGE));
        
    }

    /**
     * 扫描scanPackage下所有的类
     * @param scanPackage
     */
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.","/"));


        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                // 文件夹就遍历下一级目录
                doScanner( scanPackage + '.' + file.getName() );
            } else {
                // .class文件直接添加
                String fileName = file.getName();
                if (!fileName.endsWith(".class")) {
                    continue;
                }
                String className = scanPackage + '.' + fileName.replace(".class","");
                registyBeanClasses.add(className);
            }
        }
    }

    /**
     * 加载配置文件，把扫描到的class保存到BeanDefinition
     * @return
     */
    public List<GPBeanDefinition> loadBeanDefinitions() {

        if (registyBeanClasses.isEmpty()) {
            return null;
        }
        List<GPBeanDefinition> result = new ArrayList<>();
        try {
            for (String className : this.registyBeanClasses) {
                Class<?> clazz = Class.forName(className);
                // 如果是接口，不用保存，不能实例化
                if (clazz.isInterface()) {
                    continue;
                }
                String beanName = clazz.getSimpleName();

                result.add(doCreateBeanDefinition(toLowerFirstCase(beanName),className));

                if (Objects.isNull(clazz.getInterfaces())) {
                    continue;
                }

                // 遍历实现类的接口，根据接口注入
                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> inter : interfaces) {
                    result.add(doCreateBeanDefinition(inter.getName(),className));
                }


            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        return result;
    }

    /**
     * 根据
     * @param factoryBeanName
     * @param beanClassName
     * @return
     */
    private GPBeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName){
        GPBeanDefinition beanDefinition = new GPBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }


    /**
     * 首字母小写
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getConfig() {
        return config;
    }

}
