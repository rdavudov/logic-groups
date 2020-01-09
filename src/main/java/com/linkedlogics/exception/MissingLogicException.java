package com.linkedlogics.exception;

public class MissingLogicException extends RuntimeException {

    public MissingLogicException(String logic) {
        super("missing logic " + logic);
    }
}
