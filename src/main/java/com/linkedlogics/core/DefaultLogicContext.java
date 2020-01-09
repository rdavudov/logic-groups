package com.linkedlogics.core;

import com.linkedlogics.LogicFlowManager;
import com.linkedlogics.context.AbstractLogicContext;
import com.linkedlogics.flow.LogicExpression;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class DefaultLogicContext extends AbstractLogicContext {
    private StandardEvaluationContext evalContextParams;
    private HashMap<String, Object> contextParams ;
    private static HashMap<String, Expression> cache = new HashMap<String, Expression>() ;

    @Value("${expression.spel.compiler_mode:IMMEDIATE}")
    private SpelCompilerMode compilerMode ;

    public DefaultLogicContext(LogicFlowManager flowManager) {
        super(flowManager) ;
        evalContextParams = new StandardEvaluationContext() ;
        contextParams = new HashMap<>() ;
    }

    @Override
    public void setContextParam(String key, Object value) {
        evalContextParams.setVariable(key, value);
        contextParams.put(key, value) ;
    }

    @Override
    public Object getContextParam(String key) {
        return evalContextParams.lookupVariable(key) ;
    }

    @Override
    public Object getContextParam(String key, Object defaultValue) {
        Object value = getContextParam(key) ;
        if (value == null) {
            setContextParam(key, defaultValue);
            return defaultValue ;
        }
        return value ;
    }

    @Override
    public boolean containsContextParam(String key) {
        return contextParams.containsKey(key) ;
    }

    @Override
    public <T> T getContextParam(Class<T> key) {
        Optional<T> found = (Optional<T>) contextParams.entrySet().stream().filter(e -> e.getValue().getClass() == key).map(e -> e.getValue()).findFirst() ;
        if (found.isEmpty()) {
            found = (Optional<T>) contextParams.entrySet().stream().filter(e -> key.isAssignableFrom(e.getValue().getClass())).map(e -> e.getValue()).findFirst() ;
        }
        if (found.isPresent()) {
            return found.get() ;
        }
        return null ;
    }

    @Override
    public Stream<Map.Entry<String, Object>> stream() {
        return contextParams.entrySet().stream();
    }

    @Override
    public Object evaluate(LogicExpression expression) {
        Object result = eval(expression.getExpression()) ;
        if (result == null && expression.getDefaultValue() != null) {
            return eval(expression.getDefaultValue()) ;
        }
        return result ;
    }

    private Object eval(String expression) {
        Expression expr = cache.get(expression) ;
        if (expr == null) {
            SpelParserConfiguration config = new SpelParserConfiguration(compilerMode, null);
            expr = new SpelExpressionParser(config).parseExpression(expression) ;
            cache.put(expression, expr) ;
        }
        try {
            return expr.getValue(evalContextParams) ;
        } catch (NullPointerException| SpelEvaluationException e) {
            return null ;
        }
    }

    @Override
    public void clear() {
        super.clear();
        evalContextParams = new StandardEvaluationContext() ;
        contextParams = new HashMap<>() ;
    }
}
