package com.linkedlogics.flow.configure;

import com.linkedlogics.LogicGroupBuilder;
import com.linkedlogics.annotation.Group;
import com.linkedlogics.annotation.LogicConfiguration;
import com.linkedlogics.core.DefaultLogicGroupBuilder;
import com.linkedlogics.flow.LogicGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class AnnotatedLogicGroupConfigurer implements LogicGroupConfigurer {

    @Autowired
    private Environment environment ;
    private final AnnotatedConfigurationsProcessor configurations;

    @Override
    public List<LogicGroup> getLogicGroups(ApplicationContext context) {
        List<LogicGroup> groups = new ArrayList<>() ;

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
                String namespace = configuration.namespace().length() == 0 ? null : configuration.namespace() ;

                Method[] methods = configurationClass.getMethods();
                for (final Method method : methods) {
                    if (method.isAnnotationPresent(Group.class)) {
                        log.info("processing bean method {}", method.getName());
                        if (!validateProfile(method, environment)) {
                            log.warn("skipped bean method {}", method.getName());
                            continue;
                        }

                        Group group = method.getAnnotation(Group.class);
                        LogicGroupBuilder builder = new DefaultLogicGroupBuilder(group.value().isBlank() ? method.getName() : group.value(), context) ;
                        applyResources(group.resource(), builder);

                        LogicGroup logicGroup = null ;
                        if (Modifier.isStatic(method.getModifiers())) {
                            logicGroup = (LogicGroup) ReflectionUtils.invokeMethod(method, null, builder) ;
                        } else {
                            logicGroup = (LogicGroup) ReflectionUtils.invokeMethod(method, appContext.getBean(beanName), builder) ;
                        }

                        if (!group.parent().isBlank()) {
                            logicGroup.setParent(group.parent());
                        }

                        logicGroup.setPrecedence(group.precedence());
                        logicGroup.setNamespace(namespace);

                        groups.add(logicGroup) ;
                    }
                }
            }

            context = context.getParent() ;
        }

        return groups ;
    }

    private void applyResources(String[] resources, LogicGroupBuilder builder) {
        for (int i = 0; i < resources.length; i++) {
            try {
                builder.resource(resources[i]) ;
            } catch (IOException e) {
                throw new RuntimeException(e) ;
            }
        }
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
