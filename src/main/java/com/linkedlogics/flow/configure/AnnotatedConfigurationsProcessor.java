package com.linkedlogics.flow.configure;

import com.linkedlogics.annotation.LogicConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;

public class AnnotatedConfigurationsProcessor implements BeanPostProcessor {
    private HashMap<String, Class> configurationMap = new HashMap<>() ;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(LogicConfiguration.class)) {
            configurationMap.put(beanName, bean.getClass());
        }
        return bean;
    }

    public Class getClass(String beanName) {
        return configurationMap.get(beanName) ;
    }
}
