package com.linkedlogics;

import com.linkedlogics.context.Result;

public interface Context {

    Result execute() ;

    void setContextParam(String key, Object value) ;

    String getСontextId() ;

    void setEntry(String entry) ;

    void setEntry(String entry, String namespace) ;
}
