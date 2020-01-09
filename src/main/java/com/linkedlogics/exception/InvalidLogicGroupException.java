package com.linkedlogics.exception;

public class InvalidLogicGroupException extends RuntimeException {

    public InvalidLogicGroupException(String logic) {
        super("invalid logic group " + logic);
    }
}
