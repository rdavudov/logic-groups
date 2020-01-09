package com.linkedlogics.exception;

import com.linkedlogics.flow.LogicSeverity;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogicException extends RuntimeException {
    private long errorCode ;
    private String errorMessage ;
    private LogicSeverity severity ;

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

    public long getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LogicSeverity getSeverity() {
        return severity;
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
