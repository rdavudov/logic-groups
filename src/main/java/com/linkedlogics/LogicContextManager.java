package com.linkedlogics;

public interface LogicContextManager {

    ExecutableContext getContext(String externalId) ;

    ExecutableContext newContext() ;
}
