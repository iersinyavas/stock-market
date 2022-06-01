package com.artsoft.stock.service;

import com.artsoft.stock.model.Portfolio;
import com.artsoft.stock.model.thread.Customer;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableScheduling
public class CustomerService {

    private Customer createCustomer(){
        return new Customer(Thread.currentThread().getName(), new Portfolio(Thread.currentThread().getName()), SystemConstants.CUSTOMER_SALARY);
    }

//    @Scheduled(cron = "0 */5 * ? * *")
//    public void customerSalaryPayment() {
//        Database.customerMap.keySet().stream().forEach(s -> {
//            log.info("Key: {}", s);
//            Database.customerMap.get(s).salaryPayment();
//        });
//    }
}
