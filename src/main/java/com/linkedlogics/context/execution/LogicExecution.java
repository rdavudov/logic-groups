package com.linkedlogics.context.execution;

import lombok.Data;

import java.util.Map;

@Data
public class LogicExecution {
    private String name;
    private int tab;
    private Map<String, Object> returned;
    private Throwable exception;
    private long started = System.currentTimeMillis();
    private long finished;
    private ExecutionResult result;
}
