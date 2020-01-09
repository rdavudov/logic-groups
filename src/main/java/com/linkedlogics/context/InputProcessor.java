package com.linkedlogics.context;

import com.linkedlogics.context.validator.LogicValidator;
import com.linkedlogics.flow.LogicExpression;
import com.linkedlogics.flow.LogicGroup;
import com.linkedlogics.flow.LogicItem;

public class InputProcessor implements LogicValidator {

    @Override
    public boolean execute(LogicItem item, AbstractLogicContext context) {
        if (!(item instanceof LogicGroup)) {
            context.getInputs().clear();
            context.getOutputs().clear();

            context.getItemStack().stream().forEach(group -> {
                // we collect all inputs from top to bottom by overwriting old one
                group.getInputs().entrySet().forEach(e -> {
                    if (e.getValue() instanceof LogicExpression) {
                        context.getInputs().put(e.getKey(), context.evaluate((LogicExpression) e.getValue()));
                    } else {
                        context.getInputs().put(e.getKey(), e.getValue());
                    }

                });
                // no need to consider group outputs because output key must be at action
                // to be outputed since its there there is no need to duplicately define it
            });

            item.getInputs().entrySet().forEach(e -> {
                if (e.getValue() instanceof LogicExpression) {
                    context.getInputs().put(e.getKey(), context.evaluate((LogicExpression) e.getValue()));
                } else {
                    context.getInputs().put(e.getKey(), e.getValue());
                }
            });

            item.getOutputs().entrySet().forEach(e -> {
                context.getOutputs().put(e.getKey(), e.getValue());
            });
        }

        return true ;
    }

    @Override
    public int getOrder() {
        return 40 ;
    }
}
