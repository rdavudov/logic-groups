package com.linkedlogics.flow;

import com.linkedlogics.flow.order.LogicOrder;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class LogicItem {
    protected String name ;
    protected String execute ;
    protected String label ;
    protected String description ;
    protected String namespace ;
    protected String undo ;

    protected LogicOrder order ;
    protected int precedence ;
    protected LogicExpression condition ;
    protected String profiles ;
    protected LogicSeverity severity = LogicSeverity.low ;

    protected Map<String, Boolean> flags = new HashMap<>() ;

    protected Map<String, Object> inputs = new HashMap<>();
    protected Map<String, Object> outputs = new HashMap<>();

    protected LogicRetry retry ;

    protected Set<String> tags = new HashSet<>();
    protected Set<String> untags = new HashSet<>();
    protected Set<String> iftags = new HashSet<>();
    protected Set<String> errortags = new HashSet<>();

    protected Set<String> exportedKeys ;
    protected Set<Class> exportedClasses ;

    public LogicItem(String name, String execute) {
        this.name = name ;
        this.execute = execute ;
    }

    public void setInput(String key, Object value) {
        inputs.put(key, value) ;
    }

    public void setOutput(String key, Object value) {
        outputs.put(key, value) ;
    }

    public void setFlag(String flag, Boolean value) {
        flags.put(flag, value) ;
    }

    public Boolean getFlag(String flag) {
        Boolean value = flags.get(flag) ;
        return value != null ? value : false ;
    }

    public void merge(LogicItem item) {
        if (item.getDescription() != null) {
            setDescription(item.getDescription());
        }

        if (item.getExecute() != null) {
            setExecute(item.getExecute());
        }

        if (item.getNamespace() != null) {
            setNamespace(item.getNamespace());
        }

        if (item.getUndo() != null) {
            setUndo(item.getUndo());
        }

        if (item.getOrder() != null) {
            setOrder(item.getOrder());
        }

        if (item.getCondition() != null) {
            setCondition(item.getCondition());
        }

        if (item.getProfiles() != null) {
            setProfiles(item.getProfiles());
        }

        if (item.getFlags() != null && item.getFlags().size() > 0) {
            getFlags().putAll(item.getFlags());
        }

        if (item.getSeverity() != null) {
            setSeverity(item.getSeverity());
        }

        if (item.getRetry() != null) {
            setRetry(item.getRetry());
        }

        if (item.getInputs() != null && item.getInputs().size() > 0) {
            getInputs().putAll(item.getInputs());
        }

        if (item.getOutputs() != null && item.getOutputs().size() > 0) {
            getOutputs().putAll(item.getOutputs());
        }

        if (item.getTags() != null) {
            setTags(item.getTags());
        }

        if (item.getUntags() != null) {
            setUntags(item.getUntags());
        }

        if (item.getIftags() != null) {
            setIftags(item.getIftags());
        }

        if (item.getErrortags() != null) {
            setErrortags(item.getErrortags());
        }

        if (item.getExportedClasses() != null) {
            setExportedClasses(item.getExportedClasses());
        }

        if (item.getExportedKeys() != null) {
            setExportedKeys(item.getExportedKeys());
        }
    }

    public void print() {
        print("");
    }

    public void print(String tab) {
        System.out.println(tab + " ITEM  " + name + " exec[" + execute + "]");
    }

    public String toString() {
        return "ACTION " + name + " exec[" + execute + "]" ;
    }
}
