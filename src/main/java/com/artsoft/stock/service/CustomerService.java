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

    @Scheduled(cron = "*/5 * * * * *")
    private void createCustomer(){
        while (true){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Customer customer = new Customer(new Portfolio(), SystemConstants.CUSTOMER_SALARY);
            Database.customerMap.put(customer.getName(), customer);
            customer.start();
            log.info("{} oluştu.", customer.getCustomerName());
        }
    }

//    @Scheduled(cron = "0 */5 * ? * *")
//    public void customerSalaryPayment() {
//        Database.customerMap.keySet().stream().forEach(s -> {
//            log.info("Key: {}", s);
//            Database.customerMap.get(s).salaryPayment();
//        });
//    }
}
