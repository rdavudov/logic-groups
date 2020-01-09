package com.linkedlogics.context.processor;

import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.flow.LogicGroup;
import com.linkedlogics.flow.LogicItem;

import java.util.HashMap;

public class ExportProcessor implements LogicProcessor {

    @Override
    public void execute(LogicItem item, AbstractLogicContext context) {
        HashMap<String, Object> exported = new HashMap<>();
        if (item.getExportedKeys() != null) {
            item.getExportedKeys().forEach(k -> {
                if (context.containsContextParam(k)) {
                    context.getExported().put(k, context.getContextParam(k)) ;
                }
            });
        }

        if (item.getExportedClasses() != null) {
            context.stream().forEach(e -> {
                if (item.getExportedClasses().contains(e.getValue().getClass())) {
                    context.getExported().put(e.getKey(), e.getValue()) ;
                }
            });
        }
    }

    @Override
    public int getOrder() {
        return 30 ;
    }
}
