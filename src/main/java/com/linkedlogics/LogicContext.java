package com.linkedlogics;

import java.util.List;

public interface LogicContext {
    String getСontextId() ;

    Object getContextParam(String key) ;

    <T> T getContextParam(Class<T> key) ;

    boolean containsContextParam(String key) ;

    Object getContextParam(String key, Object defaultValue) ;

    long getContextTime() ;

    Object getInputParam(String key) ;

    Object getInputParam(String key, Object defaultValue) ;

    void setContextParam(String key, Object value) ;

    Object evaluate(String expression) ;
}
