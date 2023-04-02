package com.artsoft.stock.util;


import com.artsoft.stock.util.GeneralEnumeration.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Random;

@Slf4j
public class RandomData {

    private static Random random = new Random();

    public static BigDecimal randomLot(BigDecimal limit){
        if (limit.compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }
        int randomLot = random.nextInt(limit.intValue()+1);
        return randomLot == 0 ? BigDecimal.ONE : BigDecimal.valueOf(randomLot);
    }

    public static BigDecimal randomStartPrice(){
        return BigDecimal.valueOf(random.nextInt(5)+1);
    }

    public static BigDecimal randomShareOrderPrice(BigDecimal minPrice, BigDecimal maxPrice){
        minPrice = minPrice.multiply(BigDecimal.valueOf(100));
        maxPrice = maxPrice.multiply(BigDecimal.valueOf(100)).add(BigDecimal.ONE);
        return minPrice.add(BigDecimal.valueOf(random.nextInt(maxPrice.subtract(minPrice).intValue()))).divide(BigDecimal.valueOf(100));
    }

    public static ShareOrderType shareOrderType(){
        return ShareOrderType.values()[random.nextInt(ShareOrderType.values().length)];
    }

    public static ShareOrderStatus shareOrderStatus(){
        return ShareOrderStatus.values()[random.nextInt(ShareOrderStatus.values().length)];
    }

    public static ShareCode shareCode(){
        return ShareCode.values()[random.nextInt(ShareCode.values().length)];
    }

    public static int directionFlag(){
        return random.nextInt(DirectionFlag.values().length);
    }

    public static int random(int bound){
        return random.nextInt(bound);
    }
}
