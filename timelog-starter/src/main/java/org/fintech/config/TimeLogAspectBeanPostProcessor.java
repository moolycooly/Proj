package org.fintech.config;

import org.aopalliance.intercept.MethodInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TimeLogAspectBeanPostProcessor implements BeanPostProcessor {
    Map<String,List<Method>> methodHasAnnotation = new HashMap<>();
    Map<String,Boolean> classHasAnnotation = new HashMap<>();
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(Timelog.class)) {
            classHasAnnotation.put(beanName,true);
        }
        Method[] methods = bean.getClass().getMethods();
        for(Method method : methods) {
            if(method.isAnnotationPresent(Timelog.class)) {
                if(methodHasAnnotation.containsKey(beanName)) {
                    methodHasAnnotation.get(beanName).add(method);
                }
                else {
                    List<Method> listMethod = new ArrayList<>();
                    listMethod.add(method);
                    methodHasAnnotation.put(beanName, listMethod);
                }
            }
        }
        return bean;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(classHasAnnotation.containsKey(beanName)
                || methodHasAnnotation.containsKey(beanName)) {
            ProxyFactory proxyFactory = new ProxyFactory(bean);
            proxyFactory.setProxyTargetClass(true);
            proxyFactory.addAdvice((MethodInterceptor) invocation -> {
                Method method = invocation.getMethod();
                if(classHasAnnotation.containsKey(beanName)
                        || methodHasAnnotation
                        .get(beanName).stream()
                        .anyMatch(beanMethod -> beanMethod.getName().equals(method.getName()) &&
                                equalParamTypes(beanMethod.getParameterTypes(), method.getParameterTypes()))) {
                    Logger logger = LoggerFactory.getLogger(bean.getClass());
                    long startTime = System.currentTimeMillis();
                    logger.info("In Method: {}", invocation.getMethod().getName());
                    Object returned = invocation.proceed();
                    long endTime = System.currentTimeMillis();
                    logger.info("Out Method: {} , time: {} ms", invocation.getMethod().getName(), endTime - startTime);
                    return returned;
                }
                return invocation.proceed();
            });
            return proxyFactory.getProxy();
        }
        return bean;
    }
    boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2) {
        if (params1.length == params2.length) {
            for (int i = 0; i < params1.length; i++) {
                if (params1[i] != params2[i])
                    return false;
            }
            return true;
        }
        return false;
    }

}
