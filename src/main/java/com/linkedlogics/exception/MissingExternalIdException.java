package com.linkedlogics.exception;

public class MissingExternalIdException extends RuntimeException {

    public MissingExternalIdException() {
        super("external id is missing");
    }
}
