package com.linkedlogics.flow.configure;

import com.linkedlogics.flow.LogicGroup;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

import java.util.List;

public interface LogicGroupConfigurer extends Configurer, Ordered {
    List<LogicGroup> getLogicGroups(ApplicationContext context) ;
}
