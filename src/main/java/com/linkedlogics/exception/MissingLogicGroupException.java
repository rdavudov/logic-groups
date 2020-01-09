package com.linkedlogics.exception;

public class MissingLogicGroupException extends RuntimeException {

    public MissingLogicGroupException(String logic) {
        super("missing logic group " + logic);
    }
}
