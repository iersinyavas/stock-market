package com.artsoft.stock.config;

import com.artsoft.stock.util.RandomData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public RandomData randomData(){
        return new RandomData();
    }

}
