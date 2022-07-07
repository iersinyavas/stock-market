package com.artsoft.stock.util;

import org.springframework.stereotype.Component;



@Component
public class GeneralEnumeration {

    protected GeneralEnumeration(){};

    public enum ShareOrderStatus {
        BUY, SELL

    }

    public enum ShareOrderOperationStatus{
        CREATED, SENT, PROCESSING, REMOVE, REMAINING;
    }

    public enum ShareSessionType{
        OPENING, NORMAL, CLOSING
    }

    public enum InvestmentResult{
        PROFIT, LOSS
    }

    public enum ShareOrderType{
        LIMIT, MARKET
    }

    public enum DirectionFlag{
        UP, DOWN
    }

}
