package com.linkedlogics.config;

import com.linkedlogics.LogicContext;
import com.linkedlogics.LogicContextFactory;
import com.linkedlogics.LogicContextManager;
import com.linkedlogics.LogicFlowManager;
import com.linkedlogics.context.InputProcessor;
import com.linkedlogics.context.processor.ExportProcessor;
import com.linkedlogics.context.processor.LogicProcessor;
import com.linkedlogics.context.processor.TagProcessor;
import com.linkedlogics.context.validator.*;
import com.linkedlogics.core.DefaultContextFactory;
import com.linkedlogics.core.DefaultContextManager;
import com.linkedlogics.core.DefaultFlowManager;
import com.linkedlogics.core.DefaultLogicContext;
import com.linkedlogics.flow.configure.*;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class LinkedLogicsConfiguration {
    @Bean
    public LogicFlowManager flowManager() {
        return new DefaultFlowManager() ;
    }

    @Bean
    @Scope("prototype")
    public LogicContext context(LogicFlowManager flowManager) {
        return new DefaultLogicContext(flowManager) ;
    }

    @Bean
    public LogicContextFactory contextFactory() {
        return new DefaultContextFactory() ;
    }

    @Bean
    public LogicContextManager contextManager(LogicContextFactory factory) {
        return new DefaultContextManager(factory) ;
    }

    @Bean
    public LogicValidator inputProcessor() {
        return new InputProcessor() ;
    }

    @Bean
    public LogicProcessor exportProcessor() {
        return new ExportProcessor() ;
    }

    @Bean
    public LogicProcessor tagProcessor() {
        return new TagProcessor() ;
    }

    @Bean
    public LogicValidator anchorValidator() {
        return new AnchorValidator() ;
    }

    @Bean
    public LogicValidator conditionValidator() {
        return new ConditionValidator() ;
    }

    @Bean
    public LogicValidator enableValidator() {
        return new EnableValidator() ;
    }

    @Bean
    public LogicValidator tagValidator() {
        return new TagValidator() ;
    }

    @Bean
    public LogicValidator profileValidator(Environment environment) {
        return new ProfileValidator(environment) ;
    }

    @Bean
    public LogicConfigurer annotatedLogics() {
        return new AnnotatedLogicConfigurer((AnnotatedConfigurationsProcessor) annotatedConfigurationsProcessor(), (DefaultContextManager) contextManager(contextFactory())) ;
    }

    @Bean
    public LogicGroupConfigurer annotatedLogicGroups() {
        return new AnnotatedLogicGroupConfigurer((AnnotatedConfigurationsProcessor) annotatedConfigurationsProcessor()) ;
    }

    @Bean
    public BeanPostProcessor annotatedConfigurationsProcessor() {
        return (BeanPostProcessor) new AnnotatedConfigurationsProcessor() ;
    }
}
