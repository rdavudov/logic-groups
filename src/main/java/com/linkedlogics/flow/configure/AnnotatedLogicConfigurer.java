package com.linkedlogics.flow.configure;

import com.linkedlogics.annotation.AsyncLogic;
import com.linkedlogics.annotation.Logic;
import com.linkedlogics.annotation.LogicConfiguration;
import com.linkedlogics.annotation.RetryLogic;
import com.linkedlogics.context.executable.AsyncExecutable;
import com.linkedlogics.context.executable.LogicExecutable;
import com.linkedlogics.context.executable.MethodExecutable;
import com.linkedlogics.context.executable.RetryExecutable;
import com.linkedlogics.core.DefaultContextManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.util.HashMap;

@RequiredArgsConstructor
@Slf4j
public class AnnotatedLogicConfigurer implements LogicConfigurer {

    @Autowired
    private Environment environment ;
    private final AnnotatedConfigurationsProcessor configurations;
    private final DefaultContextManager contextManager ;

    @Override
    public HashMap<String, LogicExecutable> getLogics(ApplicationContext context) {
        HashMap<String, LogicExecutable> logics = new HashMap<String, LogicExecutable>();
        while (context != null) {
            final ApplicationContext appContext = context;
            String[] beanNames = appContext.getBeanNamesForAnnotation(LogicConfiguration.class);

            for (final String beanName : beanNames) {
                Class configurationClass = configurations.getClass(beanName);
                log.info("processing bean {} of type {}", beanName, configurationClass.getName());
                if (!validateProfile(configurationClass, environment)) {
                    log.warn("skipped bean {} because of profile");
                    continue;
                }

                LogicConfiguration configuration = (LogicConfiguration) configurationClass.getAnnotation(LogicConfiguration.class) ;
                String namespace = configuration.namespace().length() == 0 ? "" : configuration.namespace() + "." ;

                Method[] methods = configurationClass.getMethods();
                for (final Method method : methods) {
                    if (method.isAnnotationPresent(Logic.class)) {
                        log.info("processing bean method {}", method.getName());
                        if (!validateProfile(method, environment)) {
                            log.warn("skipped bean method {}", method.getName());
                            continue;
                        }

                        Logic logic = method.getAnnotation(Logic.class);
                        LogicExecutable executable = null ;

                        executable = new MethodExecutable(appContext.getBean(beanName), method) ;

                        if (method.isAnnotationPresent(RetryLogic.class)) {
                            executable = new RetryExecutable(method.getAnnotation(RetryLogic.class), executable) ;
                        }

                        if (method.isAnnotationPresent(AsyncLogic.class)) {
                            executable = new AsyncExecutable(contextManager, executable) ;
                        }

                        logics.putIfAbsent(namespace + (logic.value().isBlank() ? method.getName() : logic.value()), executable);
                    }
                }
            }

            context = context.getParent() ;
        }

        return logics;
    }



    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
