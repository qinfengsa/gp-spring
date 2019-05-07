package com.qinfengsa.springframework.web.servlet;

import com.qinfengsa.springframework.annotation.GPRequestParam;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 适配器
 * @author: qinfengsa
 * @date: 2019/4/25 22:42
 */
public class GPHandlerAdapter {

    /**
     * 判断是否适配
     * @param handler
     * @return
     */
    public boolean supports(Object handler) {
        return (handler instanceof GPHandlerMapping);
    }

    /**
     * 根据http请求获取对应的返回结果
     * @param request
     * @param response
     * @param handler
     * @return
     */
    public GPModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws  Exception {

        // 强转，Spring会有多种HandlerAdapt适配不同的HandlerMapping()
        GPHandlerMapping handlerMapping = (GPHandlerMapping) handler;

        // 创建一个参数map
        Map<String,Integer> paramsIndexMap = new HashMap<>();

        // 获取方法的参数的注解,注解是个数组（有多个参数，每个参数可能有多个注解）
        Annotation[][] paramsAnno = handlerMapping.getMethod().getParameterAnnotations();

        for (int i = 0; i < paramsAnno.length; i++) {

            for (Annotation a : paramsAnno[i]) {
                if (a instanceof GPRequestParam) {
                    GPRequestParam requestParam = (GPRequestParam) a;
                    String paramName = requestParam.value();
                    if (StringUtils.isNotBlank(paramName)) {

                        paramsIndexMap.put(paramName,i);
                    }
                }
            }
        }

        // 提取方法中的request和response参数（如果有request和response参数）
        Class<?>[] paramsTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramsTypes.length ; i ++) {
            Class<?> paramsType = paramsTypes[i];
            if (paramsType == HttpServletRequest.class || paramsType == HttpServletResponse.class) {
                paramsIndexMap.put(paramsType.getName(),i);
            }
        }

        // 获取方法的参数
        Map<String,String[]> params = request.getParameterMap();

        Object[] paramValues = new Object[paramsIndexMap.size()];

        for (Map.Entry<String,String[]> param : params.entrySet()) {
            // 参数列表中不存在，跳过
            if (!paramsIndexMap.containsKey(param.getKey())) {
                continue;
            }

            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]","")
                    .replaceAll("\\s",",");

            Integer index = paramsIndexMap.get(param.getKey());
            paramValues[index] = convert(paramsTypes[index],value);

        }

        if(paramsIndexMap.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramsIndexMap.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }

        if(paramsIndexMap.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramsIndexMap.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }

        // 反射调用method
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);
        if (result == null || result instanceof Void){
            return null;
        }


        // 返回ModelAndView
        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == GPModelAndView.class;
        if (isModelAndView) {
            return (GPModelAndView) result;
        }

        return null;
    }

    /**
     * 通过类型自定义转换
     * @param type
     * @param value
     * @return
     */
    private Object convert(Class<?> type,String value) {


        //如果是int
        if(Integer.class == type || int.class == type){
            return Integer.valueOf(value);
        }
        else if(Double.class == type || double.class == type){
            return Double.valueOf(value);
        }
        return value;
    }
}
