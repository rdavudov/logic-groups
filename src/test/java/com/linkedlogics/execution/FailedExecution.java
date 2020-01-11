package com.linkedlogics.execution;

import com.linkedlogics.Application;
import com.linkedlogics.LogicContextManager;
import com.linkedlogics.LogicFlowManager;
import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.context.Result;
import com.linkedlogics.core.DefaultFlowManager;
import com.linkedlogics.core.DefaultLogicGroupBuilder;
import com.linkedlogics.flow.LogicFlags;
import com.linkedlogics.flow.LogicGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = Application.class)
public class FailedExecution {

    @Autowired
    private LogicContextManager contextManager ;
    @Autowired
    private LogicFlowManager flowManager ;

    @Test
    public void basicFailure() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1")
                .logic("error1", "error").input("error_code", 1L).input("error_message", "error")
                .logic("add2", "add").input("item", "item2")
                .logic("error2", "error").input("error_code", 1L).input("error_message", "error").high()
                .logic("add3", "add").input("item", "item3")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert !result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 2 ;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item2")) ;
    }

    @Test
    public void groupFailure() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .group("group1")
                .logic("add1", "add").input("item", "item1")
                .logic("error1", "error").input("error_code", 1L).input("error_message", "error").medium()
                .logic("add2", "add").input("item", "item2")
                .logic("add3", "add").input("item", "item3")
                .finish()
                .group("group2")
                .logic("add4", "add").input("item", "item4")
                .logic("error2", "error").input("error_code", 1L).input("error_message", "error").low()
                .logic("add5", "add").input("item", "item5")
                .logic("error3", "error").input("error_code", 1L).input("error_message", "error").high()
                .logic("add6", "add").input("item", "item6")
                .finish()
                .group("group3")
                .logic("add7", "add").input("item", "item7")
                .logic("add8", "add").input("item", "item8")
                .logic("add9", "add").input("item", "item9")
                .finish()
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert !result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item4", "item5")) ;
    }


    @Test
    public void groupFailureAnchor() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .group("group1")
                .logic("add1", "add").input("item", "item1")
                .logic("error1", "error").input("error_code", 1L).input("error_message", "error").medium()
                .logic("add2", "add").input("item", "item2")
                .logic("add3", "add").input("item", "item3").anchor()
                .finish()
                .logic("error1", "error").input("error_code", 1L).input("error_message", "error").high()
                .group("group2")
                .logic("add4", "add").input("item", "item4")
                .logic("add5", "add").input("item", "item5").anchor()
                .logic("add6", "add").input("item", "item6")
                .finish()
                .group("group3").anchor()
                .logic("add7", "add").input("item", "item7")
                .logic("add8", "add").input("item", "item8").anchor()
                .logic("add9", "add").input("item", "item9")
                .finish()
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item3", "item8")) ;
    }

    @Test
    public void groupFailureAnchorQuite() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .group("group1")
                .logic("add1", "add").input("item", "item1")
                .logic("error1", "error").input("error_code", 1L).input("error_message", "error").medium()
                .logic("add2", "add").input("item", "item2")
                .logic("add3", "add").input("item", "item3").anchor()
                .finish()
                .logic("error1", "error").input("error_code", 1L).input("error_message", "error").high()
                .group("group2")
                .logic("add4", "add").input("item", "item4")
                .logic("add5", "add").input("item", "item5").anchor()
                .logic("add6", "add").input("item", "item6")
                .finish()
                .group("group3").anchor()
                .logic("add7", "add").input("item", "item7")
                .logic("add8", "add").input("item", "item8").anchor()
                .logic("add9", "add").input("item", "item9")
                .finish()
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item3", "item8")) ;
    }

    @Test
    public void groupFatalFailureAnchorQuite() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .group("group1")
                .logic("add1", "add").input("item", "item1")
                .logic("error1", "error").input("error_code", -100L).input("error_message", "error").fatal()
                .logic("add2", "add").input("item", "item2")
                .logic("add3", "add").input("item", "item3").anchor()
                .finish()
                .logic("error1", "error").input("error_code", 1L).input("error_message", "error").high()
                .group("group2")
                .logic("add4", "add").input("item", "item4")
                .logic("add5", "add").input("item", "item5").anchor()
                .logic("add6", "add").input("item", "item6")
                .finish()
                .group("group3").anchor()
                .logic("add7", "add").input("item", "item7")
                .logic("add8", "add").input("item", "item8").anchor()
                .logic("add9", "add").input("item", "item9")
                .finish()
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert !result.isSuccess() ;
        assert result.getErrorCode() == -100L ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 1 ;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1")) ;
    }
}
