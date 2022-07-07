package com.artsoft.stock.service;

import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.model.Portfolio;
import com.artsoft.stock.model.share.ShareCertificate;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.model.thread.Customer;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;

@Service
@Slf4j
@EnableScheduling
public class CustomerService {



//    @Scheduled(cron = "0 */5 * ? * *")
//    public void customerSalaryPayment() {
//        Database.customerMap.keySet().stream().forEach(s -> {
//            log.info("Key: {}", s);
//            Database.customerMap.get(s).salaryPayment();
//        });
//    }
}
