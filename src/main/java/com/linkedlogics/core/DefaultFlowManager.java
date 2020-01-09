package com.linkedlogics.core;

import com.linkedlogics.LogicFlowManager;
import com.linkedlogics.context.executable.LogicExecutable;
import com.linkedlogics.context.LogicSorter;
import com.linkedlogics.flow.LogicFlow;
import com.linkedlogics.flow.LogicGroup;
import com.linkedlogics.flow.configure.LogicConfigurer;
import com.linkedlogics.flow.configure.LogicGroupConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultFlowManager implements LogicFlowManager {
    private AtomicInteger version = new AtomicInteger(0) ;
    private LogicFlow flow;
    private List<LogicFlow> versions = new ArrayList<>() ;

    @Autowired
    private ApplicationContext context ;
    @Autowired
    private List<LogicConfigurer> actionConfigurers ;
    @Autowired
    private List<LogicGroupConfigurer> actionGroupConfigurers ;

    @Override
    public LogicFlow getFlow() {
        return flow;
    }

    @EventListener
    public void onContextRefreshed(ContextRefreshedEvent event) {
        this.context = event.getApplicationContext() ;
        flow = create() ;
    }

    public LogicFlow create() {
        flow = new LogicFlow() ;
        flow.setVersion(version.incrementAndGet());
        flow.setLogics(getLogics(context));
        flow.setRoot(new LogicSorter().sort(organizeLogicGroups(getLogicGroups(context))));
        return flow;
    }

    public LogicFlow create(LogicGroup group) {
        flow = new LogicFlow() ;
        flow.setVersion(version.incrementAndGet());
        flow.setLogics(getLogics(context));
        flow.setRoot(new LogicSorter().sort(group));
        return flow;
    }

    protected HashMap<String, LogicExecutable> getLogics(ApplicationContext applicationContext) {
        HashMap<String, LogicExecutable> actions = new HashMap<String, LogicExecutable>();

        for (LogicConfigurer configurer : actionConfigurers) {
            actions.putAll(configurer.getLogics(applicationContext));
        }

        return actions ;
    }

    public List<LogicGroup> getLogicGroups(ApplicationContext applicationContext) {
        List<LogicGroup> groups = new ArrayList<LogicGroup>() ;

        for (LogicGroupConfigurer configurer : actionGroupConfigurers) {
            groups.addAll(configurer.getLogicGroups(applicationContext));
        }

        return groups ;
    }

    public LogicGroup organizeLogicGroups(List<LogicGroup> list) {
        list.sort(new Comparator<LogicGroup>() {
            public int compare(LogicGroup o1, LogicGroup o2) {
                return o1.getPrecedence() - o2.getPrecedence();
            }
        });
        LogicGroup root = new LogicGroup(null) ;
        int lastSize = list.size() ;
        while (list.size() > 0) {
            Iterator<LogicGroup> iterator = list.iterator() ;
            while (iterator.hasNext()) {
                LogicGroup group = iterator.next() ;
                if (group.getParent() == null) {
                    root.add(group);
                    iterator.remove();
                } else {
                    LogicGroup parent = root.find(group.getParent(), group.getNamespace()) ;
                    if (parent != null) {
                        parent.add(group);
                        iterator.remove();
                    }
                }
            }

            if (list.size() > 0 && lastSize == list.size()) {
                throw new RuntimeException("missing parent " + list.get(0).getParent() + " for group " + list.get(0).getName()) ;
            }

            lastSize = list.size() ;
        }

        return root ;
    }


}
