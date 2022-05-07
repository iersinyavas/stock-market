package com.artsoft.stock.exception;

public class WrongLotInformationException extends Exception {

    @Override
    public String getMessage() {
        return "1 den az lot girilemez.";
    }
}
