package com.linkedlogics.context.validator;

import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.flow.LogicItem;
import org.springframework.core.Ordered;

public interface LogicValidator extends Ordered {
    boolean execute(LogicItem item, AbstractLogicContext context) ;

    String getName() ;

    @Override
    default int getOrder() {
        return 0;
    }
}
