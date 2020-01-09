package com.linkedlogics.execution;

import com.linkedlogics.Application;
import com.linkedlogics.LogicContextManager;
import com.linkedlogics.LogicFlowManager;
import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.context.Result;
import com.linkedlogics.core.DefaultFlowManager;
import com.linkedlogics.core.DefaultLogicGroupBuilder;
import com.linkedlogics.exception.MissingLogicException;
import com.linkedlogics.exception.MissingLogicGroupException;
import com.linkedlogics.flow.LogicFlags;
import com.linkedlogics.flow.LogicGroup;
import com.linkedlogics.flow.LogicSelection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = Application.class)
public class BasicExecution {

    @Autowired
    private LogicContextManager contextManager ;
    @Autowired
    private LogicFlowManager flowManager ;

    @Test
    public void basicExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
            .logic("add1", "add").input("itema", "item1")
            .logic("add2", "add").input("item", "item2")
            .logic("add3", "add").input("item", "item3")
            .export("list")
            .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;
        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
            containsAll(List.of("item1", "item2", "item3")) ;
    }

    @Test
    public void groupExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
            .group("group1")
            .logic("add1", "add").input("item", "item1")
            .logic("add2", "add").input("item", "item2")
            .finish()
            .group("group2")
            .logic("add3", "add").input("item", "item3")
            .logic("add4", "add").input("item", "item4")
            .finish()
            .group("group3")
            .logic("add5", "add").input("item", "item5")
            .logic("add6", "add").input("item", "item6")
            .finish()
            .build() ;
        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;

        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 6 ;
        assert ((List<String>) context.getContextParam("list")).
            containsAll(List.of("item1", "item2", "item3", "item4", "item5", "item6")) ;
    }

    @Test
    public void groupSingleMatchExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
            .group("group1").selection(LogicSelection.executeOne)
            .logic("add1", "add").input("item", "item1")
            .logic("add2", "add").input("item", "item2")
            .finish()
            .group("group2").selection(LogicSelection.executeOne)
            .logic("add3", "add").input("item", "item3")
            .logic("add4", "add").input("item", "item4")
            .finish()
            .group("group3").selection(LogicSelection.executeOne)
            .logic("add5", "add").input("item", "item5")
            .logic("add6", "add").input("item", "item6")
            .finish()
            .build() ;
        

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
            containsAll(List.of("item1", "item3", "item5")) ;
    }

    @Test
    public void groupConditionalExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
            .group("group1").selection(LogicSelection.executeOne)
            .logic("add1", "add").input("item", "item1")
            .logic("add2", "add").input("item", "item2")
            .finish()
            .group("group2").selection(LogicSelection.executeOne)
            .logic("add3", "add").input("item", "item3")
            .logic("add4", "add").input("item", "item4")
            .finish()
            .group("group3").selection(LogicSelection.executeOne)
            .logic("add5", "add").input("item", "item5")
            .logic("add6", "add").input("item", "item6")
            .finish()
            .build() ;
        

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
            containsAll(List.of("item1", "item3", "item5")) ;
    }

    @Test
    public void groupSingleExecutionExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
            .group("group1").selection(LogicSelection.executeOne)
            .logic("error1", "error").input("error_code", 1L).input("error_message", "error")
            .logic("add1", "add").input("item", "item1")
            .logic("add2", "add").input("item", "item2")
            .finish()
            .group("group2").selection(LogicSelection.executeAny)
            .logic("error2", "error").input("error_code", 1L).input("error_message", "error")
            .logic("add3", "add").input("item", "item3")
            .logic("add4", "add").input("item", "item4")
            .finish()
            .group("group3").selection(LogicSelection.executeAll)
            .logic("error3", "error").input("error_code", 1L).input("error_message", "error")
            .logic("add5", "add").input("item", "item5")
            .logic("add6", "add").input("item", "item6")
            .finish()
            .build() ;
        

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
            containsAll(List.of("item3", "item5", "item6")) ;
    }

    @Test
    public void groupConditionalSequentialExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
            .group("group1").selection(LogicSelection.executeOne)
            .logic("error1", "error").input("error_code", 1L).input("error_message", "error")
            .logic("add1", "add").input("item", "item1")
            .logic("add2", "add").input("item", "item2")
            .finish()
            .group("group2").selection(LogicSelection.executeAny)
            .logic("error2", "error").input("error_code", 1L).input("error_message", "error")
            .logic("add3", "add").input("item", "item3")
            .logic("add4", "add").input("item", "item4")
            .finish()
            .group("group3").selection(LogicSelection.executeAll)
            .logic("error3", "error").input("error_code", 1L).input("error_message", "error")
            .logic("add5", "add").input("item", "item5")
            .logic("add6", "add").input("item", "item6")
            .finish()
            .build() ;
        

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
            containsAll(List.of("item3", "item5", "item6")) ;
    }

    @Test
    public void groupSingleExecutionConditionalExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
            .group("group1").selection(LogicSelection.executeOne)
            .logic("error1", "error").input("error_code", 1L).input("error_message", "error")
            .logic("add1", "add").input("item", "item1")
            .logic("add2", "add").input("item", "item2")
            .finish()
            .group("group2").selection(LogicSelection.executeAny)
            .logic("error2", "error").input("error_code", 1L).input("error_message", "error")
            .logic("add3", "add").input("item", "item3").condition("false")
            .logic("add4", "add").input("item", "item4")
            .logic("add5", "add").input("item", "item5")
            .finish()
            .group("group3").selection(LogicSelection.executeAll)
            .logic("error3", "error").input("error_code", 1L).input("error_message", "error")
            .logic("add6", "add").input("item", "item6")
            .logic("add7", "add").input("item", "item7").condition("false")
            .logic("add8", "add").input("item", "item8")
            .finish()
            .build() ;
        

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
            containsAll(List.of("item4", "item6", "item8")) ;
    }

    @Test
    public void groupSingleExecutionGroupConditionalExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
            .group("group1").selection(LogicSelection.executeOne)
            .logic("error1", "error").input("error_code", 1L).input("error_message", "error")
            .logic("add1", "add").input("item", "item1")
            .logic("add2", "add").input("item", "item2")
            .finish()
            .group("group2").selection(LogicSelection.executeAny)
            .logic("error2", "error").input("error_code", 1L).input("error_message", "error")
            .logic("add3", "add").input("item", "item3").condition("false")
            .logic("add4", "add").input("item", "item4")
            .logic("add5", "add").input("item", "item5")
            .finish()
            .group("group3").selection(LogicSelection.executeAll).condition("false")
            .logic("error3", "error").input("error_code", 1L).input("error_message", "error")
            .logic("add6", "add").input("item", "item6")
            .logic("add7", "add").input("item", "item7").condition("false")
            .logic("add8", "add").input("item", "item8")
            .finish()
            .build() ;
        

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;
        System.out.println(context.getContextParam("list"));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 1 ;
        assert ((List<String>) context.getContextParam("list")).
            containsAll(List.of("item4")) ;
    }

    @Test
    public void basicDisabledExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
            .logic("add1", "add").input("item", "item1")
            .logic("add2", "add").input("item", "item2").flag(LogicFlags.IS_DISABLED)
            .logic("add3", "add").input("item", "item3")
            .build() ;
        

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;

        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 2 ;
        assert ((List<String>) context.getContextParam("list")).
            containsAll(List.of("item1", "item3")) ;
    }

    @Test
    public void groupDisabledExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
            .group("group1")
            .logic("add1", "add").input("item", "item1")
            .logic("add2", "add").input("item", "item2")
            .finish()
            .group("group2").flag(LogicFlags.IS_DISABLED)
            .logic("add3", "add").input("item", "item3")
            .logic("add4", "add").input("item", "item4")
            .finish()
            .group("group3")
            .logic("add5", "add").input("item", "item5").flag(LogicFlags.IS_DISABLED)
            .logic("add6", "add").input("item", "item6")
            .finish()
            .build() ;
        

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;

        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
            containsAll(List.of("item1", "item2", "item6")) ;
    }

    @Test
    public void missingActionExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
            .logic("add1", "add").input("item", "item1")
            .logic("add2", "no_add").input("item", "item2")
            .logic("add3", "add").input("item", "item3")
            .build() ;

        Assertions.assertThrows(MissingLogicException.class, () -> {
            AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
            Result result = context.executeGroup(root) ;
        }) ;
    }
}
