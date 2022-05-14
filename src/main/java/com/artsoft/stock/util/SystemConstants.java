package com.artsoft.stock.util;

import java.math.BigDecimal;

public interface SystemConstants {
    public static final Integer MAX_LOT = 100;
    public static final Integer CUSTOMER_SALARY = 2000;
    public static final Integer CUSTOMER_BALANCE = 2000;
    public static final Integer START_HAVE_SHARE_LOT = 1000;
    public static final Integer CUSTOMER_RANDOM_SLEEP = 5000;
    public static final Integer SHARE_ORDER_PROCESS_SLEEP = 1;
    public static final BigDecimal FREE_CAPITAL_INCREASE_RATE = BigDecimal.valueOf(100).setScale(2);
}
