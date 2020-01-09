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
}
