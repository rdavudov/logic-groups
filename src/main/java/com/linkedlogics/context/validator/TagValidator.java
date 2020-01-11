package com.linkedlogics.context.validator;


import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.flow.LogicItem;

public class TagValidator implements LogicValidator {

    @Override
    public boolean execute(LogicItem item, AbstractLogicContext context) {
        if (item.getIftags().size() > 0) {
            if (!context.getTags().containsAll(item.getIftags())) {
                return false;
            }
        }

        if (item.getIfanytag().size() > 0) {
            if (context.getTags().stream().filter(t -> item.getIfanytag().contains(t)).count() == 0) {
                return false ;
            }
        }

        if (item.getIfnotags().size() > 0) {
            if (context.getTags().stream().filter(t -> item.getIfnotags().contains(t)).count() > 0) {
                return false ;
            }
        }

        return true ;
    }

    @Override
    public int getOrder() {
        return 5;
    }
}
