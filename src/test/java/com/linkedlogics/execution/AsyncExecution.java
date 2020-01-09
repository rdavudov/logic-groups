package com.linkedlogics.execution;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedlogics.Application;
import com.linkedlogics.LogicContextManager;
import com.linkedlogics.LogicFlowManager;
import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.context.Result;
import com.linkedlogics.core.DefaultContextManager;
import com.linkedlogics.core.DefaultFlowManager;
import com.linkedlogics.core.DefaultLogicGroupBuilder;
import com.linkedlogics.exception.InvalidLogicOrderException;
import com.linkedlogics.flow.LogicFlags;
import com.linkedlogics.flow.LogicGroup;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest(classes = Application.class)
public class AsyncExecution {
    
    @Autowired
    private LogicContextManager contextManager ;
    @Autowired
    private LogicFlowManager flowManager ;

    @Test
    public void asyncExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1")
                .logic("request1", "request")
                .logic("response1", "response")
                .logic("add2", "add").input("item", "item2")
                .build() ;

        ((DefaultFlowManager) flowManager).create(root) ;
        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        assert result.isAsync() ;

        context = (AbstractLogicContext) contextManager.getContext("test") ;
        assert context != null ;
        context.setContextParam("item", "item0");
        result = context.execute() ;
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item0" , "item2")) ;
    }

    @Test
    public void asyncThreadedAfterExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1")
                .logic("request1", "request")
                .logic("response1", "response")
                .logic("add2", "add").input("item", "item2")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        Result result = context.execute() ;

        assert result.isSuccess() ;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AbstractLogicContext context = (AbstractLogicContext) contextManager.getContext("test") ;
                assert context != null ;
                context.setContextParam("item", "item0");
                Result result = context.execute() ;
            }
        }) ;
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(context.getContextParam("list"));
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2", "item0")) ;
    }

    @Test
    public void asyncThreadedBeforeExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1")
                .logic("request1", "request")
                .logic("response1", "response")
                .logic("add2", "add").input("item", "item2")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AbstractLogicContext context = (AbstractLogicContext) contextManager.getContext("test") ;
                assert context != null ;
                context.setContextParam("item", "item0");
                Result result = context.execute() ;
            }
        }) ;
        t.start();

        Result result = context.execute() ;
        assert result.isSuccess() ;

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(context.getContextParam("list"));
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2", "item0")) ;
    }

    @Test
    public void asyncThreadedNotExpireExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1")
                .logic("request1", "request")
                .logic("response1", "response")
                .logic("add2", "add").input("item", "item2")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        Result result = context.execute() ;

        assert result.isSuccess() ;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ((DefaultContextManager) contextManager).expireContexts();
                AbstractLogicContext context = (AbstractLogicContext) contextManager.getContext("test") ;
                assert context != null ;
                context.setContextParam("item", "item0");
                Result result = context.execute() ;
            }
        }) ;
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(context.getContextParam("list"));
        assert context.isExecuted() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2", "item0")) ;
    }

    @Test
    public void asyncThreadedExpireExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1")
                .logic("request1", "request")
                .logic("response1", "response")
                .logic("add2", "add").input("item", "item2")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        Result result = context.execute() ;

        assert result.isSuccess() ;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ((DefaultContextManager) contextManager).expireContexts();
                ((DefaultContextManager) contextManager).expireContexts();
                AbstractLogicContext context = (AbstractLogicContext) contextManager.getContext("test") ;
                assert context == null ;
            }
        }) ;
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(context.getContextParam("list"));
        assert context.getContextParam("list") != null ;
        assert !context.isExecuted() ;
        assert ((List<String>) context.getContextParam("list")).size() == 1 ;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1")) ;
    }
}
