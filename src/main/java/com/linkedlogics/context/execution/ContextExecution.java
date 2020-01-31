package com.linkedlogics.context.execution;

import com.linkedlogics.exception.LogicException;

import java.util.ArrayList;
import java.util.List;

public class ContextExecution {
    private List<LogicExecution> executions = new ArrayList<>();

    public void addExecution(LogicExecution execution) {
        executions.add(execution) ;
    }
}
