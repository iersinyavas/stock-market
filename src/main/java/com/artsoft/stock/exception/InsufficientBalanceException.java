package com.artsoft.stock.exception;

public class InsufficientBalanceException extends Exception {

    @Override
    public String getMessage() {
        return "Yetersiz bakiye";
    }
}
