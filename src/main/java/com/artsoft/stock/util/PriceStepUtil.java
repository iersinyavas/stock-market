package com.artsoft.stock.util;

import java.math.BigDecimal;

public class PriceStepUtil {

    public static BigDecimal priceControlForStep(BigDecimal price){
        if(price.compareTo(BigDecimal.valueOf(20)) < 0){
            return BigDecimal.valueOf(0.01);
        }else if (price.compareTo(BigDecimal.valueOf(50)) < 0) {
            return BigDecimal.valueOf(0.02);
        }else if (price.compareTo(BigDecimal.valueOf(100)) < 0) {
            return BigDecimal.valueOf(0.05);
        }else {
            return BigDecimal.valueOf(0.1);
        }
    }
}
