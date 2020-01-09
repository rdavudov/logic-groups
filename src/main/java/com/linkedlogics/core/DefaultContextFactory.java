package com.linkedlogics.core;

import com.linkedlogics.LogicContext;
import com.linkedlogics.LogicContextFactory;
import com.linkedlogics.context.AbstractLogicContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultContextFactory implements LogicContextFactory {
    private ConcurrentHashMap<Thread, AbstractLogicContext> contextMap = new ConcurrentHashMap<Thread, AbstractLogicContext>() ;

    @Autowired
    private ApplicationContext appContext ;

    @Override
    public AbstractLogicContext createContext() {
        AbstractLogicContext context = contextMap.get(Thread.currentThread()) ;

        if (context != null) {
            if (context.isSaved()) {
                context = null ;
            } else {
                context.clear();
            }
        }

        if (context == null) {
            context = (AbstractLogicContext) appContext.getBean(LogicContext.class) ;
            contextMap.put(Thread.currentThread(), context) ;
        }

        return context ;
    }

    @Scheduled(fixedRateString = "${linkedlogics.context_map_clear_time:60000}")
    protected void clean() {
        contextMap.keySet().stream().filter(t -> t.isInterrupted()).forEach(t -> contextMap.remove(t)) ;
    }
}
