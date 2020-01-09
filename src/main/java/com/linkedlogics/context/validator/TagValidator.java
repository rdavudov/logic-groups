package com.linkedlogics.context.validator;


import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.flow.LogicItem;

public class TagValidator implements LogicValidator {

    @Override
    public boolean execute(LogicItem item, AbstractLogicContext context) {
        if (context.getTags().containsAll(item.getIftags())) {
            return true ;
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 5;
    }
}
