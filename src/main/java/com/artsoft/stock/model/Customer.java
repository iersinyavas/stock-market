package com.artsoft.stock.model;

import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.SystemConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
public class Customer extends Thread{
    private String customerName;
    private Portfolio portfolio;
    private BigDecimal salary;
    private BlockingQueue<BigDecimal> salaryQueue = new LinkedBlockingQueue<>();

    @JsonIgnore
    private Random random = new Random();

    public Customer(String customerName, Portfolio portfolio, BigDecimal salary){
        this.customerName = customerName;
        this.portfolio = portfolio;
        this.salary = salary;
        Database.processedShareOrders.put(customerName, new LinkedBlockingQueue<>());
    }

    public void salaryPayment(){
        this.getPortfolio().salaryPayment(this.getSalary());
        log.info("Maaşlar yattı");
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(random.nextInt(SystemConstants.CUSTOMER_RANDOM_SLEEP));
                ShareOrder shareOrder = this.getPortfolio().createShareOrder();
                if (Objects.nonNull(shareOrder)){
                    this.getPortfolio().sendShareOrder(shareOrder);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
