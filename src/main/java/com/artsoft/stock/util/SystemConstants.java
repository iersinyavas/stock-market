package com.artsoft.stock.util;

import com.artsoft.stock.repository.Database;

import java.math.BigDecimal;

public interface SystemConstants {
    public static final Integer MAX_LOT = 10;
    public static final BigDecimal CUSTOMER_SALARY = BigDecimal.valueOf(100);
    public static final BigDecimal CUSTOMER_BALANCE = BigDecimal.valueOf(10000);
    public static final Integer START_HAVE_SHARE_LOT = 1000;
    public static final Integer SHARE_LOT = 100000;
    public static final Integer CUSTOMER_RANDOM_SLEEP = 10000;
    public static final Integer CUSTOMER_CREATE = 10000;
    public static final Integer SHARE_ORDER_PROCESS_SLEEP = 1;
    public static final BigDecimal FREE_CAPITAL_INCREASE_RATE = BigDecimal.valueOf(100).setScale(2);
}
