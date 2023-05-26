package com.artsoft.stock.exception;

public class InsufficientLotException extends Exception {

    @Override
    public String getMessage() {
        return "Yetersiz lot";
    }
}
