package com.linkedlogics.context.validator;

import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.flow.LogicFlags;
import com.linkedlogics.flow.LogicItem;

public class ConditionValidator implements LogicValidator {

    @Override
    public boolean execute(LogicItem item, AbstractLogicContext context) {
        if (item.getCondition() != null) {
            return (Boolean) context.evaluate(item.getCondition()) ;
        }
        return true ;
    }

    @Override
    public int getOrder() {
        return 30 ;
    }
}
