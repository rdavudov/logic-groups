package com.linkedlogics.context.processor;

import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.flow.LogicItem;

public class TagProcessor implements LogicProcessor {
    @Override
    public void execute(LogicItem item, AbstractLogicContext context) {
        for (String untag : item.getUntags()) {
            context.getTags().remove(untag) ;
        }

        for (String tag : item.getTags()) {
            context.getTags().add(tag) ;
        }
    }

    @Override
    public int getOrder() {
        return 40 ;
    }
}
