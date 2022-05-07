package com.artsoft.stock.model;

import com.artsoft.stock.repository.Database;
import lombok.*;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Customer {
    private String name;
    private Portfolio portfolio;
    private BigDecimal salary;
    private BlockingQueue<BigDecimal> salaryQueue = new LinkedBlockingQueue<>();

    public Customer(String name, Portfolio portfolio, BigDecimal salary){
        this.name = name;
        this.portfolio = portfolio;
        this.salary = salary;
        Database.processedShareOrders.put(name, new LinkedBlockingQueue<>());
    }
}
