package com.artsoft.stock.config;

import com.artsoft.stock.util.RandomData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class BeanConfiguration {

    @Bean
    public RandomData randomData(){
        return new RandomData();
    }
}
