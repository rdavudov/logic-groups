package com.linkedlogics.flow;

import com.linkedlogics.context.executable.LogicExecutable;

import java.util.HashMap;
import java.util.Optional;

public class LogicFlow {
    private LogicGroup root ;
    private int version ;

    private HashMap<String, LogicExecutable> actions = new HashMap<String, LogicExecutable>();

    public LogicGroup getRoot() {
        return root ;
    }

    public void setRoot(LogicGroup root) {
        this.root = root;
    }

    public Optional<LogicExecutable> getLogic(String action) {
        return Optional.ofNullable(actions.get(action)) ;
    }

    public void setLogic(String action, LogicExecutable executable) {
        actions.put(action, executable) ;
    }

    public void setLogics(HashMap<String, LogicExecutable> actions) {
        this.actions = actions;
    }

    public HashMap<String, LogicExecutable> getLogics() {
        return actions;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


}
