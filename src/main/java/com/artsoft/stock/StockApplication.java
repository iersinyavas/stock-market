package com.artsoft.stock;

import com.artsoft.stock.batch.ShareOrderCreator;
import com.artsoft.stock.batch.ShareOrderMatcher;
import com.artsoft.stock.batch.TreaderCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockApplication implements CommandLineRunner {
    @Autowired
    private ShareOrderCreator shareOrderCreator;
    @Autowired
    private TreaderCreator treaderCreator;
    @Autowired
    private ShareOrderMatcher shareOrderMatcher;
    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        treaderCreator.start();
        shareOrderCreator.start();
        shareOrderMatcher.start();
    }
}
