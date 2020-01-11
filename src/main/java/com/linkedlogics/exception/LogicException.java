package com.linkedlogics.exception;

import com.linkedlogics.flow.LogicSeverity;
import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@Getter
public class LogicException extends RuntimeException {
    private long errorCode ;
    private String errorMessage ;
    private LogicSeverity severity ;
    private HashMap<String, Object> params = new HashMap<>();
    private HashSet<String> tags = new HashSet<>() ;
    private HashSet<String> untags = new HashSet<>() ;

    public LogicException(long errorCode, String errorMessage) {
        super(errorMessage) ;
        this.errorCode = errorCode ;
        this.errorMessage = errorMessage ;
    }

    public LogicException(long errorCode, String errorMessage, LogicSeverity severity) {
        super(errorMessage) ;
        this.errorCode = errorCode ;
        this.errorMessage = errorMessage ;
        this.severity = severity ;
    }

    public LogicException(Throwable e) {
        super(e.getLocalizedMessage());
        this.errorCode = -1 ;
        this.errorMessage = exceptionToString(e) ;
    }

    public LogicException(Throwable e, LogicSeverity severity) {
        super(e.getLocalizedMessage());
        this.errorCode = -1 ;
        this.errorMessage = exceptionToString(e) ;
        this.severity = severity ;
    }

    public static String exceptionToString(Throwable e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString() ;
    }


    public LogicException setExceptionParam(String name, Object value) {
        params.put(name, value) ;
        return this ;
    }

    public LogicException setExceptionTag(String... tags) {
        this.tags.addAll(Arrays.asList(tags)) ;
        return this ;
    }

    public LogicException setExceptionUntag(String... tags) {
        this.untags.addAll(Arrays.asList(tags)) ;
        return this ;
    }

    public static LogicException fatal(long errorCode, String errorMessage) {
        return new LogicException(errorCode, errorMessage, LogicSeverity.fatal) ;
    }

    public static LogicException fatal(Throwable e) {
        return new LogicException(e, LogicSeverity.fatal) ;
    }

    public static LogicException high(long errorCode, String errorMessage) {
        return new LogicException(errorCode, errorMessage, LogicSeverity.high) ;
    }

    public static LogicException high(Throwable e) {
        return new LogicException(e, LogicSeverity.high) ;
    }

    public static LogicException medium(long errorCode, String errorMessage) {
        return new LogicException(errorCode, errorMessage, LogicSeverity.medium) ;
    }

    public static LogicException medium(Throwable e) {
        return new LogicException(e, LogicSeverity.medium) ;
    }

    public static LogicException low(long errorCode, String errorMessage) {
        return new LogicException(errorCode, errorMessage, LogicSeverity.low) ;
    }

    public static LogicException low(Throwable e) {
        return new LogicException(e, LogicSeverity.low) ;
    }
}
