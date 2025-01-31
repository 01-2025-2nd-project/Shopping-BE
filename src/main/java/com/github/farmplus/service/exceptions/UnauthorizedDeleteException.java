package com.github.farmplus.service.exceptions;

public class UnauthorizedDeleteException extends RuntimeException{
    public UnauthorizedDeleteException(String message) {
        super(message);
    }
}
