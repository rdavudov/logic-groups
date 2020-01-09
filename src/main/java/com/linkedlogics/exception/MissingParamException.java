package com.linkedlogics.exception;

public class MissingParamException extends RuntimeException {

    public MissingParamException(String param) {
        super("missing parameter " + param);
    }
}
