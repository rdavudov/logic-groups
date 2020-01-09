package com.linkedlogics.context;

import com.linkedlogics.ExecutableContext;
import com.linkedlogics.LogicContext;
import com.linkedlogics.LogicFlowManager;
import com.linkedlogics.context.executable.AsyncExecutable;
import com.linkedlogics.context.executable.LogicExecutable;
import com.linkedlogics.context.processor.LogicProcessor;
import com.linkedlogics.context.validator.LogicValidator;
import com.linkedlogics.exception.InvalidLogicGroupException;
import com.linkedlogics.exception.LogicException;
import com.linkedlogics.exception.MissingLogicException;
import com.linkedlogics.exception.MissingLogicGroupException;
import com.linkedlogics.flow.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Stream;

public abstract class AbstractLogicContext implements LogicContext, ExecutableContext {
    /**
     * Auto generated context id
     */
    protected String contextId = UUID.randomUUID().toString() ;
    /**
     * Time context is created
     */
    protected long contextTime = System.currentTimeMillis() ;
    /**
     * Action group stack
     */
    protected Stack<LogicGroup> itemStack = new Stack<>();
    /**
     * Action group elements stack
     */
    protected Stack<ListIterator<LogicItem>> iteratorStack = new Stack<>();
    /**
     * Input parameters from actions and groups. It is populated before each action
     */
    private Map<String, Object> inputs = new HashMap<>();
    /**
     * Output parameters from actions. Output values are set in context params
     */
    private Map<String, Object> outputs = new HashMap<>();
    /**
     * Means execution chain is broken and only anchored actions/groups will be executed
     */
    private boolean isBroken ;
    /**
     * Means execution chain is cancelled and only actions within same group will be cancelled
     */
    private boolean isCancelled ;
    /**
     * Flow instance being executed
     */
    private LogicFlow flow ;
    @Autowired
    private List<LogicValidator> validators;
    @Autowired
    private List<LogicProcessor> processors;
    /**
     * Time context is updated
     */
    private long executeTime = System.currentTimeMillis() ;
    /**
     * Is context saved for async execution, which means we can reuse it
     */
    private boolean isSaved ;
    /**
     * Action group name to start execution with
     */
    private String entry;

    private String namespace ;

    private Map<String, Object> exported = new HashMap<>();

    private List<LogicItem> undo = new ArrayList<>() ;

    private Set<String> tags = new HashSet<>() ;

    @Autowired
    public AbstractLogicContext(LogicFlowManager flowManager) {
        this.flow = flowManager.getFlow() ;
    }

    /**
     * Evaluates an expression which is needed to check conditions of actions/groups and population of input/output parameters
     * @param expression
     * @return
     */
    public abstract Object evaluate(LogicExpression expression) ;

    public Object evaluate(String expression) {
        return evaluate(new LogicExpression(expression)) ;
    }

    public String getСontextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public long getContextTime() {
        return contextTime;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public boolean isBroken() {
        return isBroken;
    }

    void setBroken(boolean broken) {
        isBroken = broken;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    Stack<LogicGroup> getItemStack() {
        return itemStack;
    }

    void setItemStack(Stack<LogicGroup> itemStack) {
        this.itemStack = itemStack;
    }

    Stack<ListIterator<LogicItem>> getIteratorStack() {
        return iteratorStack;
    }

    void setIteratorStack(Stack<ListIterator<LogicItem>> iteratorStack) {
        this.iteratorStack = iteratorStack;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    Map<String, Object> getInputs() {
        return inputs;
    }

    void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    Map<String, Object> getOutputs() {
        return outputs;
    }

    void setOutputs(Map<String, Object> outputs) {
        this.outputs = outputs;
    }

    public List<LogicValidator> getValidators() {
        return validators;
    }

    public void setValidators(List<LogicValidator> validators) {
        this.validators = validators;
    }

    public List<LogicProcessor> getProcessors() {
        return processors;
    }

    public void setProcessors(List<LogicProcessor> processors) {
        this.processors = processors;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry, String namespace) {
        this.entry = entry;
        this.namespace = namespace ;
    }

    @Override
    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Map<String, Object> getExported() {
        return exported;
    }

    public abstract Stream<Map.Entry<String, Object>> stream() ;

    public Object getInputParam(String key) {
        Object input = this.inputs.get(key) ;
        if (input != null && input instanceof LogicExpression) {
            return evaluate((LogicExpression) input) ;
        }
        return input ;
    }

    public Object getInputParam(String key, Object defaultValue) {
        Object input = getInputParam(key) ;
        return input == null ? defaultValue : input ;
    }

    public void setOutputParam(String key, Object value) {
        this.outputs.put(key, value) ;
    }

    private String getNamespaced(LogicItem item) {
        if (item.getNamespace() != null) {
            return item.getNamespace() + "." + item.getExecute() ;
        } else if (itemStack.peek().getNamespace() != null) {
            return itemStack.peek().getNamespace() + "." + item.getExecute() ;
        }
        return item.getExecute() ;
    }

    /**
     * Executes context with provided flow
     * @return
     */
    public Result execute() {
        executeTime = System.currentTimeMillis() ;
        Result result = null ;

        try {
            if (itemStack.isEmpty()) {
                if (entry != null) {
                    if (flow.getRoot() instanceof LogicGroup) {
                        LogicGroup logicGroup = ((LogicGroup) flow.getRoot()).find(entry, namespace) ;
                        if (logicGroup != null) {
                            result = executeGroup(logicGroup);
                        } else {
                            throw new MissingLogicGroupException(entry) ;
                        }
                    } else {
                        throw new InvalidLogicGroupException(entry) ;
                    }
                } else {
                    result = executeGroup(flow.getRoot());
                }
            } else {
                // incase we return from async callback
                while (!itemStack.isEmpty()) {
                    result = executeGroup();

                    if (result.isAsync()) {
                        return result;
                    }
                }
            }

            if (result.isAsync()) {
                return result ;
            }
        } catch (Throwable e) {
            result = new Result(e) ;
        }

        if (!result.isSuccess()) {
            executeUndo() ;
        }

        return result ;
    }

    protected void executeUndo() {
        ListIterator<LogicItem> iterator = undo.listIterator(undo.size()) ;

        while (iterator.hasPrevious()) {
            LogicItem undoItem = iterator.previous() ;
            Optional<LogicExecutable> executable = flow.getLogic(getNamespaced(undoItem)) ;

            if (executable.isPresent()) {
                Optional<Map<String, Object>> logicResult = executeLogic(undoItem, executable.get()) ;
            }
        }
    }

    public Result executeGroup(LogicGroup group) {
        push(group);
        return executeGroup();
    }

    protected Result executeGroup() {
        LogicGroup group = itemStack.peek();
        ListIterator<LogicItem> iterator = iteratorStack.peek();
        Result result = new Result();

        item:
        while (iterator.hasNext()) {
            LogicItem item = iterator.next();
            // We validate action items for conditional, profile, broke execution etc.
            // Any custom validator can be implemented
            for (LogicValidator validator : validators) {
                if (!validator.execute(item, this)) {
                    continue item;
                }
            }

            if (item instanceof LogicGroup) {
                result = executeGroup((LogicGroup) item) ;
            } else {
                Optional<LogicExecutable> executable = flow.getLogic(getNamespaced(item)) ;

                if (executable.isPresent()) {
                    try {
                        Optional<Map<String, Object>> logicResult = executeLogic(item, executable.get()) ;

                        if (logicResult.isPresent()) {
                            Map<String, Object> map = (Map<String, Object>) logicResult.get();
                            map.entrySet().forEach((e) -> {
                                setContextParam(e.getKey(), e.getValue());
                            });
                        }

                        for (LogicProcessor processor : processors) {
                            processor.execute(item, this) ;
                        }

                        if (executable.get() instanceof AsyncExecutable) {
                            return Result.asyncResult() ;
                        }

                        if (group.getSelection() == LogicSelection.executeAny) {
                            break item;
                        }
                    } catch (LogicException e) {
                        switch (item.getSeverity()) {
                            case low:
                                break;
                            case medium:
                                setCancelled(true);
                                break;
                            case high:
                                setCancelled(true);
                                setBroken(true);
                                result = new Result(e) ;
                                break;
                            case fatal:
                                return new Result(e) ;
                        }
                    }


                } else {
                    throw new MissingLogicException(item.getExecute()) ;
                }
            }

            if (group.getSelection() == LogicSelection.executeOne) {
                break item;
            }
        }
        setCancelled(false);
        pop();

        if (result.isSuccess() && exported.size() > 0) {
            result.setExported(exported);
        }

        return result ;
    }


    protected Optional<Map<String, Object>> executeLogic(LogicItem item, LogicExecutable executable) {
        Optional<Map<String, Object>> result = executable.execute(this);

        if (item.getUndo() != null) {
            undo.add(item) ;
        }

        return result ;
    }

    private void push(LogicGroup group) {
        itemStack.push(group);
        iteratorStack.push(group.iterator());
    }

    private void pop() {
        itemStack.pop();
        iteratorStack.pop();
    }

    public boolean isExecuted() {
        System.out.println(itemStack.size());
        System.out.println(itemStack.isEmpty());
        return itemStack.isEmpty() ;
    }

    public void clear() {
        itemStack.clear();
        iteratorStack.clear();
        inputs.clear();
        outputs.clear();
        undo.clear();
        tags.clear();
        contextId = UUID.randomUUID().toString() ;
        contextTime = System.currentTimeMillis() ;
    }
}
