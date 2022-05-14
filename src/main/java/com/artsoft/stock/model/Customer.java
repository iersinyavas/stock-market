package com.artsoft.stock.model;

import com.artsoft.stock.repository.Database;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
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

    public void salaryPayment(){
        this.getPortfolio().salaryPayment(this.getSalary());
        log.info("Maaşlar yattı");
    }
}
