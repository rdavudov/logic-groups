package com.linkedlogics.execution;

import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.context.Result;
import com.linkedlogics.core.DefaultFlowManager;
import com.linkedlogics.core.DefaultLogicGroupBuilder;
import com.linkedlogics.flow.LogicGroup;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class EventDriven {
    @Test
    public void basicExecution() {
        LogicGroup root = new DefaultLogicGroupBuilder("group", null)
                .logic("create_customer", "create_customer").publish("customer_created")
                .logic("create_order", "create_order").consume("customer_created").publish("order_created")
                .logic("create_account", "create_account").consume("order_created").publish("account-created")
                .export("list")
                .build() ;

    }
}
