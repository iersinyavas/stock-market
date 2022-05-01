package com.artsoft.stock;

import com.artsoft.stock.model.Customer;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@Slf4j
public class StockApplication implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Thread customerA = new Thread(() -> {
            Customer customer = customerService.createCustomer("A");

            while (true) {

            }
        });
    }

}
