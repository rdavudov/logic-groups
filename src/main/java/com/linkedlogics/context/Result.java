package com.linkedlogics.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.linkedlogics.exception.LogicException;
import lombok.Data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@Data
public class Result {
    private boolean isSuccess = true ;
    @JsonIgnore
    private boolean isAsync = false ;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long errorCode ;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage ;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> exported ;

    public Result() {

    }

    public Result(LogicException exception) {
        isSuccess = false ;
        errorCode = exception.getErrorCode() ;
        errorMessage = exception.getErrorMessage() ;
    }

    public Result(Throwable exception) {
        isSuccess = false ;
        errorCode = -1L ;
        errorMessage = exceptionToString(exception) ;
    }

    public static String exceptionToString(Throwable e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString() ;
    }

    public static Result asyncResult() {
        Result result = new Result() ;
        result.setAsync(true);
        return result ;
    }
}
