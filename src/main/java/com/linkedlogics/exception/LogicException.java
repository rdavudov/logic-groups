package com.linkedlogics.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogicException extends RuntimeException {
    private long errorCode ;
    private String errorMessage ;

    public LogicException(long errorCode, String errorMessage) {
        super(errorMessage) ;
        this.errorCode = errorCode ;
        this.errorMessage = errorMessage ;
    }

    public LogicException(Throwable e) {
        super(e.getLocalizedMessage());
        this.errorCode = -1 ;
        this.errorMessage = exceptionToString(e) ;
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
}
