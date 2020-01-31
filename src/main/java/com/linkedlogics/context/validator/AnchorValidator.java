package com.linkedlogics.context.validator;

import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.flow.LogicFlags;
import com.linkedlogics.flow.LogicItem;

public class AnchorValidator implements LogicValidator {

    @Override
    public boolean execute(LogicItem item, AbstractLogicContext context) {
        if ((context.isCancelled() || context.isBroken()) && !item.getFlag(LogicFlags.IS_ANCHORED)) {
            return false ;
        }
        return true ;
    }

    @Override
    public String getName() {
        return "anchor";
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
