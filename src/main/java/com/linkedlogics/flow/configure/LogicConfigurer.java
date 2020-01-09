package com.linkedlogics.flow.configure;

import com.linkedlogics.context.executable.LogicExecutable;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;

import java.util.HashMap;

public interface LogicConfigurer extends Configurer, Ordered {
    public HashMap<String, LogicExecutable> getLogics(ApplicationContext context) ;
}
