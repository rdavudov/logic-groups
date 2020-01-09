package com.linkedlogics.exception;

public class InvalidExternalIdException extends RuntimeException {

    public InvalidExternalIdException() {
        super("external id is missing");
    }
}
