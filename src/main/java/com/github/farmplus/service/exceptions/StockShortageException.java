package com.github.farmplus.service.exceptions;

public class StockShortageException extends RuntimeException{
    public StockShortageException(String message) {
        super(message);
    }
}
