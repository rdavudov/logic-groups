package com.linkedlogics;

import java.util.List;

public interface LogicContext {
    String getСontextId() ;

    long getContextTime() ;

    Object getContextParam(String key) ;

    <T> T getContextParam(Class<T> key) ;

    Object getContextParam(String key, Object defaultValue) ;

    void setContextParam(String key, Object value) ;

    Object getInputParam(String key) ;

    Object getInputParam(String key, Object defaultValue) ;

    Object evaluate(String expression) ;
}
