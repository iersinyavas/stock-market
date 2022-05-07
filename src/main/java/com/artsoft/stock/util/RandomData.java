package com.artsoft.stock.util;

import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.util.GeneralEnumeration.*;
import java.math.BigDecimal;
import java.util.Random;

public class RandomData {

    private static Random random = new Random();

    public static Integer randomLot(int limit){
        int randomLot = random.nextInt(limit+1);
        return randomLot == 0 ? 1 : randomLot;
    }

    public static BigDecimal randomStartPrice(){
        return BigDecimal.valueOf(/*random.nextInt(10)+*/1);
    }

    public static BigDecimal randomShareOrderPrice(BigDecimal minPrice, BigDecimal maxPrice){
        minPrice = minPrice.multiply(BigDecimal.valueOf(100));
        maxPrice = maxPrice.multiply(BigDecimal.valueOf(100)).add(BigDecimal.ONE);
        return minPrice.add(BigDecimal.valueOf(random.nextInt(maxPrice.subtract(minPrice).intValue()))).divide(BigDecimal.valueOf(100));
    }

    public static int shareOrderStatusIndex(){
        return random.nextInt(ShareOrderStatus.values().length);
    }

    public static int shareCodeIndex(){
        return random.nextInt(ShareCode.values().length);
    }

    public static int random(){
        return random.nextInt(3);
    }
}
