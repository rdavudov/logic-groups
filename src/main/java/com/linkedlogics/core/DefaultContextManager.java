package com.linkedlogics.core;

import com.linkedlogics.Context;
import com.linkedlogics.LogicContextFactory;
import com.linkedlogics.LogicContextManager;
import com.linkedlogics.context.AbstractLogicContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultContextManager implements LogicContextManager {
    private ConcurrentHashMap<String, AbstractLogicContext> contextMap = new ConcurrentHashMap<String, AbstractLogicContext>();
    private ConcurrentHashMap<String, AbstractLogicContext> previousMap = new ConcurrentHashMap<String, AbstractLogicContext>();
    private LogicContextFactory contextFactory ;

    public DefaultContextManager(LogicContextFactory contextFactory) {
        this.contextFactory = contextFactory;
    }

    @Value("${linkedlogics.context_max_wait_time:5000}")
    private long maxWaitTime ;

    public void setContext(String externalId, AbstractLogicContext context) {
        context.setSaved(true);
        contextMap.put(externalId, context) ;
    }

    public Context getContext(String externalId) {
        return getContext(externalId, maxWaitTime) ;
    }

    private AbstractLogicContext findContext(String externalId) {
        AbstractLogicContext context = contextMap.remove(externalId) ;
        if (context == null) {
            return previousMap.remove(externalId) ;
        }
        return context ;
    }

    public Context getContext(String externalId, long maxWaitTime) {
        AbstractLogicContext context = findContext(externalId) ;
        long start = System.currentTimeMillis() ;
        try {
            while (context == null && System.currentTimeMillis() - start < maxWaitTime) {
                context = findContext(externalId) ;
                Thread.sleep(1);
            }
        } catch (InterruptedException e) { }
        return context ;
    }

    public Context newContext() {
        return contextFactory.createContext() ;
    }

    public Context newContext(String contextId) {
        AbstractLogicContext context = (AbstractLogicContext) newContext() ;
        context.setContextId(contextId);
        return context ;
    }

    @Scheduled(fixedRateString = "${linkedlogics.context_map_switch_time:60000}")
    public void expireContexts() {
        previousMap = contextMap ;
        contextMap = new ConcurrentHashMap<String, AbstractLogicContext>() ;
    }

    public int getContextCount() {
        return contextMap.size() ;
    }
}
