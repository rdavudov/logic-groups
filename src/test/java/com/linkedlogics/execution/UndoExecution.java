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
public class UndoExecution {
    
    @Autowired
    private LogicContextManager contextManager ;
    @Autowired
    private LogicFlowManager flowManager ;

    @Test
    public void undoExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "item1")
                .logic("add2", "add").input("item", "item2").undo("remove")
                .logic("add3", "add").input("item", "item3")
                .logic("error1", "error").input("error_code", 1L).input("error_message", "error").high()
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) (AbstractLogicContext) contextManager.newContext() ;
        Result result = context.execute() ;
        System.out.println(context.getContextParam("list"));
        assert !result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 2 ;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item3")) ;
    }

    @Test
    public void groupExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .group("group1")
                .logic("add1", "add").input("item", "item1")
                .logic("add2", "add").input("item", "item2").undo("remove")
                .finish()
                .group("group2")
                .logic("add3", "add").input("item", "item3")
                .logic("add4", "add").input("item", "item4").undo("remove")
                .finish()
                .group("group3")
                .logic("add5", "add").input("item", "item5")
                .logic("add6", "add").input("item", "item6").undo("remove")
                .finish()
                .logic("error1", "error").input("error_code", 1L).input("error_message", "error").high()
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        Result result = context.execute() ;

        System.out.println(context.getContextParam("list"));
        assert !result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).
                containsAll(List.of("item1", "item3", "item5")) ;
    }
}
