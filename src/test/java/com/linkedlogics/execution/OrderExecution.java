package com.linkedlogics.execution;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedlogics.Application;
import com.linkedlogics.LogicContextManager;
import com.linkedlogics.LogicFlowManager;
import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.context.Result;
import com.linkedlogics.core.DefaultFlowManager;
import com.linkedlogics.core.DefaultLogicGroupBuilder;
import com.linkedlogics.exception.InvalidLogicOrderException;
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
public class OrderExecution {
    
    @Autowired
    private LogicContextManager contextManager ;
    @Autowired
    private LogicFlowManager flowManager ;

    @Test
    public void basicExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "1").order(10)
                .logic("add2", "add").input("item", "2").order(5)
                .logic("add3", "add").input("item", "3").order(0)
                .build() ;

        ((DefaultFlowManager) flowManager).create(root) ;
        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;

        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).stream().collect(Collectors.joining()).equals("321") ;
    }

    @Test
    public void groupExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .group("group1").order(10)
                .logic("add1", "add").input("item", "1").order(10)
                .logic("add2", "add").input("item", "2").order(0)
                .finish()
                .group("group2").order(5)
                .logic("add3", "add").input("item", "3").order(10)
                .logic("add4", "add").input("item", "4").order(0)
                .finish()
                .group("group3").order(0)
                .logic("add5", "add").input("item", "5").order(10)
                .logic("add6", "add").input("item", "6").order(0)
                .finish()
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;

        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.execute() ;
        System.out.println(((List<String>) context.getContextParam("list")));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 6 ;
        assert ((List<String>) context.getContextParam("list")).stream().collect(Collectors.joining()).equals("654321") ;
        System.out.println(new JSONObject(new ObjectMapper().convertValue(root, new TypeReference<Map<String, Object>>() {})).toString(4));
    }

    @Test
    public void relativeExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "1").order(0)
                .logic("add2", "add").input("item", "2").order(1)
                .logic("add3", "add").input("item", "3").order(2)
                .logic("add4", "add").input("item", "4").order(3)
                .logic("add5", "add").input("item", "5").order(4)
                .logic("add6", "add").input("item", "6").last()
                .logic("add7", "add").input("item", "7").before("add6").last()
                .logic("add8", "add").input("item", "8").first()
                .logic("add9", "add").input("item", "9").after("add2").before("add3")
                .logic("addA", "add").input("item", "A").after("add9").before("add3")
                .logic("addB", "add").input("item", "B").after("add4").before("add5").last()
                .logic("addC", "add").input("item", "C").after("add4").before("add5")
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;
        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;
        System.out.println(((List<String>) context.getContextParam("list")).stream().collect(Collectors.joining()));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 12 ;
        assert ((List<String>) context.getContextParam("list")).stream().collect(Collectors.joining()).equals("8129A34CB576") ;
    }

    @Test
    public void relativeReversedExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "1").order(0)
                .logic("add2", "add").input("item", "2").order(1)
                .logic("add3", "add").input("item", "3").order(2)
                .logic("add4", "add").input("item", "4").order(3)
                .logic("add5", "add").input("item", "5").order(4)
                .logic("addC", "add").input("item", "C").after("add4").before("add5")
                .logic("addB", "add").input("item", "B").after("add4").before("add5").last()
                .logic("addA", "add").input("item", "A").after("add9").before("add3")
                .logic("add9", "add").input("item", "9").after("add2").before("add3")
                .logic("add8", "add").input("item", "8").first()
                .logic("add7", "add").input("item", "7").before("add6").last()
                .logic("add6", "add").input("item", "6").last()
                .build() ;
        ((DefaultFlowManager) flowManager).create(root) ;
        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;
        System.out.println(((List<String>) context.getContextParam("list")).stream().collect(Collectors.joining()));
        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 12 ;
        assert ((List<String>) context.getContextParam("list")).stream().collect(Collectors.joining()).equals("8129A34CB576") ;
    }

    @Test
    public void onlyRelativeExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "1")
                .logic("add2", "add").input("item", "2").after("add1")
                .logic("add3", "add").input("item", "3").after("add2")
                .build() ;

        ((DefaultFlowManager) flowManager).create(root) ;
        AbstractLogicContext context = (AbstractLogicContext) contextManager.newContext() ;
        context.setContextParam("list", new ArrayList<>());
        Result result = context.executeGroup(root) ;

        assert result.isSuccess() ;
        assert context.getContextParam("list") != null ;
        assert ((List<String>) context.getContextParam("list")).size() == 3 ;
        assert ((List<String>) context.getContextParam("list")).stream().collect(Collectors.joining()).equals("123") ;
    }

    @Test
    public void cycleExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("add1", "add").input("item", "1").after("add3")
                .logic("add2", "add").input("item", "2").after("add1")
                .logic("add3", "add").input("item", "3").after("add2")
                .build() ;

        Assertions.assertThrows(InvalidLogicOrderException.class, () -> {
            ((DefaultFlowManager) flowManager).create(root) ;
        }) ;
    }
}
