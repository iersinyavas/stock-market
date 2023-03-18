package com.artsoft.stock.util;

import org.springframework.stereotype.Component;


@Component
public class GeneralEnumeration {

    protected GeneralEnumeration(){};

    public enum ShareCode {
        ALPHA;// BETA, GAMA;
    }

    public enum ShareOrderStatus {

        BUY, SELL

    }

    public enum ShareOrderOperationStatus{
        CREATED, SENT, PROCESSING, REMOVE, REMAINING
    }

    public enum ShareSessionType{
        OPENING, NORMAL, CLOSING
    }

    public enum InvestmentResult{
        PROFIT, LOSS
    }

    public enum DirectionFlag{
        UP, DOWN
    }

    public enum ShareOrderType {
        MARKET, LIMIT
    }

    public enum ShareOrderSwitch{
        BUY_LIMIT, BUY_MARKET, SELL_LIMIT, SELL_MARKET
    }

}
