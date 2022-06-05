package com.artsoft.stock.model.thread;

import com.artsoft.stock.model.Portfolio;
import com.artsoft.stock.model.ShareOrder;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.SystemConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Slf4j
public class Customer extends Thread{
    private String customerName;
    private Portfolio portfolio;
    private BigDecimal salary;
    public Boolean isWait = Boolean.FALSE;
    public Object lock = new Object();

    @JsonIgnore
    private Random random = new Random();

    public Customer(Portfolio portfolio, BigDecimal salary){
        this.customerName = this.getName();
        this.portfolio = portfolio;
        this.salary = salary;
    }

    public void salaryPayment(){
        this.getPortfolio().salaryPayment(this.getSalary());
        log.info("Maaşlar yattı");
    }

    public void openLock(){
        synchronized (lock){
            lock.notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lock){
                try {
                    Thread.sleep(random.nextInt(SystemConstants.CUSTOMER_RANDOM_SLEEP));
                    if (isWait){
                        log.info("Thread: {}", Thread.currentThread().getName());
                        lock.wait();
                        isWait = Boolean.FALSE;
                    }
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
}
