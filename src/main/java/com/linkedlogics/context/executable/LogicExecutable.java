package com.linkedlogics.context.executable;

import com.linkedlogics.LogicContext;

import java.util.Map;
import java.util.Optional;

public interface LogicExecutable {
    public Optional<Map<String, Object>> execute(LogicContext context) ;
}
