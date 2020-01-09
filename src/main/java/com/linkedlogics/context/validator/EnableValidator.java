package com.linkedlogics.context.validator;

import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.flow.LogicFlags;
import com.linkedlogics.flow.LogicItem;

public class EnableValidator implements LogicValidator {
    @Override
    public boolean execute(LogicItem item, AbstractLogicContext context) {
        if (item.getFlag(LogicFlags.IS_DISABLED)) {
            return false ;
        }
        return true ;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
