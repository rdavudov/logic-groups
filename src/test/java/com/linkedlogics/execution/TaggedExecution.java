package com.linkedlogics.execution;

import com.linkedlogics.Application;
import com.linkedlogics.LogicContextManager;
import com.linkedlogics.LogicFlowManager;
import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.context.Result;
import com.linkedlogics.core.DefaultFlowManager;
import com.linkedlogics.core.DefaultLogicGroupBuilder;
import com.linkedlogics.flow.LogicGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = Application.class)
public class TaggedExecution {
    
    @Autowired
    private LogicContextManager contextManager ;
    @Autowired
    private LogicFlowManager flowManager ;

    @Test
    public void taggedExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1").tag("t1")
                .logic("add2", "add").input("item", "item2")
                .logic("add3", "add").input("item", "item3").iftags("t1")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2", "item3")) ;
    }

    @Test
    public void untaggedExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1").tag("t1")
                .logic("add2", "add").input("item", "item2").untag("t1")
                .logic("add3", "add").input("item", "item3").iftags("t1")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 2;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2")) ;
    }

    @Test
    public void missingTagExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1").tag("t1")
                .logic("add2", "add").input("item", "item2")
                .logic("add3", "add").input("item", "item3").iftags("t1", "t2")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 2;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2")) ;
    }

    @Test
    public void errorTagExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1")
                .logic("error1", "error").input("error_code", 1L).input("error_message", "error").errortags("e1")
                .logic("add2", "add").input("item", "item2")
                .logic("add3", "add").input("item", "item3").iftags("e1")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2", "item3")) ;
    }

    @Test
    public void falseErrorTagExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1").errortags("e1")
                .logic("add2", "add").input("item", "item2")
                .logic("add3", "add").input("item", "item3").iftags("e1")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 2;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2")) ;
    }

    @Test
    public void alltagsExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1").tag("t1")
                .logic("add2", "add").input("item", "item2").tag("t2")
                .logic("add3", "add").input("item", "item3").iftags("t1", "t2")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2", "item3")) ;
    }

    @Test
    public void alltagsFailedExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1").tag("t1")
                .logic("add2", "add").input("item", "item2")
                .logic("add3", "add").input("item", "item3").iftags("t1", "t2")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 2;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2")) ;
    }

    @Test
    public void anytagsExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1").tag("t1")
                .logic("add2", "add").input("item", "item2")
                .logic("add3", "add").input("item", "item3").ifanytag("t1", "t2")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2", "item3")) ;
    }

    @Test
    public void notagsExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1").tag("t1")
                .logic("add2", "add").input("item", "item2")
                .logic("add3", "add").input("item", "item3").ifnotags("t1", "t2")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 2;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2")) ;
    }

    @Test
    public void notagsSuccessExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1")
                .logic("add2", "add").input("item", "item2")
                .logic("add3", "add").input("item", "item3").ifnotags("t1", "t2")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2", "item3")) ;
    }
}
