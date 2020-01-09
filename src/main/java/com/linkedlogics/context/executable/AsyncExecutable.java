package com.linkedlogics.context.executable;

import com.linkedlogics.LogicContext;
import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.core.DefaultContextManager;
import com.linkedlogics.exception.MissingExternalIdException;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class AsyncExecutable implements LogicExecutable {
    private final DefaultContextManager contextManager ;
    private final LogicExecutable executable ;

    @Override
    public Optional<Map<String, Object>> execute(LogicContext context) {
        Optional<Map<String, Object>> result = executable.execute(context) ;

        if (result.isPresent()) {
            String externalId = (String) result.get().get("externalId") ;
            if (externalId == null) {
                throw new MissingExternalIdException() ;
            } else {
                contextManager.setContext(externalId, (AbstractLogicContext) context);
            }
        } else {
            throw new MissingExternalIdException() ;
        }

        return result ;
    }
}
