package com.linkedlogics.context.executable;

import com.linkedlogics.LogicContext;
import com.linkedlogics.exception.MissingParamException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Slf4j
public class MethodParameter {
    public enum Source {
        context, input, both, self
    }

    protected boolean isRequired ;
    protected String defaultValue ;
    protected String name ;
    protected Class type ;
    protected Source source ;


    private MethodParameter(String name, Class type, Source source, boolean isRequired, String defaultValue) {
        this.name = name ;
        this.type = type ;
        this.source = source ;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue != null && defaultValue.trim().length() > 0 ? defaultValue : null ;
    }

    protected Object value(LogicContext context) {
       switch (source) {
           case self:
               return context ;
           case context:
               if (name != null && name.trim().length() > 0) {
                   return context.getContextParam(name) ;
               } else {
                   return context.getContextParam(type) ;
               }
           case input:
               return context.getInputParam(name) ;
           case both:
               return context.getContextParam(name, context.getInputParam(name)) ;
       }

       return null ;
    }

    public Object getValue(LogicContext context) {
        Object value = value(context) ;
        if (name != null && value == null && Collection.class.isAssignableFrom(type)) {
            value = createCollection(type) ;
            context.setContextParam(name, value);
        }

        if (value == null) {
            if (isRequired) {
                throw new MissingParamException(name);
            } else if (defaultValue != null) {
                return getDefaultValue(type) ;
            }
        }
        return value ;
    }

    public Object createCollection(Class type) {
        if (type == List.class) {
            return new ArrayList() ;
        } else if (type == Set.class) {
            return new HashSet() ;
        } else {
            try {
                return type.getConstructor().newInstance() ;
            } catch (Throwable e) {
                log.error("error in creating collection of type " + type.getName(), e);
            }
        }
        return null;
    }

    public static Object getDefaultValue(Class type) {
        if (type == List.class || List.class.isAssignableFrom(type)) {
            return new ArrayList<>() ;
        } else if (type == Set.class) {
            return new HashSet<>() ;
        } else if (type == Map.class) {
            return new HashMap<>() ;
        }
        return null;
    }

    public static MethodParameter getContextAsParam() {
        return new MethodParameter(null, null, Source.self, false, null) ;
    }

    public static MethodParameter getContextParam(String name, Class type, boolean isRequired, String defaultValue) {
        return new MethodParameter(name, type, Source.context, isRequired, defaultValue) ;
    }

    public static MethodParameter getInputParam(String name, Class type, boolean isRequired, String defaultValue) {
        return new MethodParameter(name, type, Source.input, isRequired, defaultValue) ;
    }
}
