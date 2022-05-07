package com.artsoft.stock.exception;

public class NotHaveShareException extends Exception {

    @Override
    public String getMessage() {
        return "Belirtilen miktarda lota sahip değilsiniz";
    }
}
