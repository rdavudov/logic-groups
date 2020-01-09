package com.linkedlogics.context.processor;

import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.flow.LogicItem;
import org.springframework.core.Ordered;

public interface LogicProcessor extends Ordered {
    public void execute(LogicItem item, AbstractLogicContext context) ;

    @Override
    default int getOrder() {
        return 0;
    }
}
