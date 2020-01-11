package com.linkedlogics;

public interface LogicContextManager {

    Context getContext(String externalId) ;

    Context newContext() ;
}
