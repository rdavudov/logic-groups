package com.linkedlogics.context.executable;

import com.linkedlogics.LogicContext;
import com.linkedlogics.annotation.AsyncLogic;
import com.linkedlogics.annotation.ContextParam;
import com.linkedlogics.annotation.InputParam;
import com.linkedlogics.exception.MissingParamException;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class MethodExecutable implements LogicExecutable {
    private Object object ;
    private Method method ;
    private String returns ;

    private ParamValue[] values ;
    private DefaultValue[] defaultValues ;

    public MethodExecutable(Method method) {
        this(null, method) ;
    }

    public MethodExecutable(Object object, Method method) {
        this.object = object;
        this.method = method;
        init();
    }

    private void init() {
        values = new ParamValue[this.method.getParameterCount()] ;



        for (int i = 0; i < this.method.getParameterCount(); i++) {
            if (this.method.getParameterTypes()[i] == LogicContext.class
                || LogicContext.class.isAssignableFrom(this.method.getParameterTypes()[i])) {
                values[i] = new ContextItself() ;
                continue;
            }

            Annotation[] annotations = this.method.getParameterAnnotations()[i] ;
            if (annotations.length == 0) {
                values[i] = new ContextParamByClass(true, false, null, this.method.getParameterTypes()[i]) ;
            } else {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof ContextParam) {
                        ContextParam param = (ContextParam) annotation ;
                        values[i] = new ContextParamByName(param.required(), param.nonNull(), param.value(), this.method.getParameterTypes()[i]) ;
                    } else if (annotation instanceof InputParam) {
                        InputParam param = (InputParam) annotation ;
                        values[i] = new InputParamByName(param.required(), param.nonNull(), param.value(), this.method.getParameterTypes()[i]) ;
                    }
                }
            }
        }
    }

    @Override
    public Optional<Map<String, Object>> execute(LogicContext context) {
        Object[] params = new Object[values.length] ;
        for (int i = 0; i < values.length; i++) {
            params[i] = values[i].getValue(context) ;
        }
        final Object result ;
        if (Modifier.isStatic(method.getModifiers())) {
            result = ReflectionUtils.invokeMethod(method, null, params);
        } else {
            result = ReflectionUtils.invokeMethod(method, object, params);
        }

        if (result != null) {
            if (method.isAnnotationPresent(AsyncLogic.class)) {
                return Optional.of(new HashMap<>() {{
                    put("externalId", result.toString()) ;
                }});
            }

            if (result instanceof Map) {
                return Optional.of((Map<String, Object>) result) ;
            } else {
                String key = returns == null ? result.getClass().getSimpleName().toLowerCase() : returns ;
                return Optional.of(new HashMap<>() {{
                   put(key, result) ;
                }});
            }
        }

        return Optional.empty() ;
    }

    private abstract class ParamValue {
        protected boolean isRequired ;
        protected boolean isNonNull ;
        protected String name ;
        protected Class type ;

        public ParamValue(boolean isRequired, boolean isNonNull, String name, Class type) {
            this.isRequired = isRequired;
            this.isNonNull = isNonNull ;
            this.name = name ;
            this.type = type ;
        }

        protected abstract Object value(LogicContext context) ;

        public Object getValue(LogicContext context) {
            Object value = value(context) ;
            if (value == null) {
                if (isRequired) {
                    throw new MissingParamException(name);
                } else if (isNonNull) {
                    return getDefaultValue(type) ;
                }
            }
            return value ;
        }
    }

    private class ContextParamByName extends ParamValue {

        public ContextParamByName(boolean isRequired, boolean isNonNull, String name, Class type) {
            super(isRequired, isNonNull, name, type) ;
        }

        @Override
        public Object value(LogicContext context) {
            return context.getContextParam(name) ;
        }
    }

    private class ContextParamByClass extends ParamValue {

        public ContextParamByClass(boolean isRequired, boolean isNonNull, String name, Class type) {
            super(isRequired, isNonNull, name, type) ;
        }

        @Override
        public Object value(LogicContext context) {
            return context.getContextParam(type) ;
        }
    }

    private class ContextItself extends ParamValue {

        public ContextItself() {
            super(false,false, null, null) ;
        }

        @Override
        public Object value(LogicContext context) {
            return context ;
        }
    }

    private class InputParamByName extends ParamValue {

        public InputParamByName(boolean isRequired, boolean isNonNull, String name, Class type) {
            super(isRequired, isNonNull, name, type) ;
        }

        @Override
        public Object value(LogicContext context) {
            return context.getInputParam(name) ;
        }
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
}
