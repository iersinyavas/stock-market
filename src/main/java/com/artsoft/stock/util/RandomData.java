package com.artsoft.stock.util;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

public class RandomData {

    private static Random random = new Random();

    public static Integer randomLot(int limit){
        return random.nextInt(limit)+1;
    }
}
