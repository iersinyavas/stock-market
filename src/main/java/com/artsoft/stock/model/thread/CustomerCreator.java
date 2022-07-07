package com.artsoft.stock.model.thread;

import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.model.Portfolio;
import com.artsoft.stock.model.Share;
import com.artsoft.stock.model.share.ShareCertificate;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.service.CustomerService;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.SystemConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Slf4j
public class CustomerCreator extends Thread {

    public Boolean isWait = Boolean.FALSE;
    public Object lock = new Object();
    private Random random = new Random();

    public void openLock(){
        synchronized (lock){
            lock.notify();
        }
    }

    public CustomerCreator(String name) {
        super(name);
    }

    @Override
    public void run() {
        while (true){
            synchronized (lock){
                try {
                    if (isWait){
                        log.info("Thread: {}", Thread.currentThread().getName());
                        lock.wait();
                        isWait = Boolean.FALSE;
                    }
                    Thread.sleep(random.nextInt(SystemConstants.CUSTOMER_CREATE));
                    this.createCustomer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void createCustomer(){
        Customer customer = new Customer(new Portfolio(), SystemConstants.CUSTOMER_SALARY);
        Database.customerMap.put(customer.getName(), customer);
        BlockingQueue<ShareCertificate> shareCertificates = Database.shareMap.get(ShareCode.ALPHA).buyFromCompany(RandomData.randomLot(SystemConstants.START_HAVE_SHARE_LOT));
        while (!shareCertificates.isEmpty()){
            try {
                ShareCertificate shareCertificate = shareCertificates.take();
                customer.getPortfolio().subtractBalance(shareCertificate.getPrice());
                customer.getPortfolio().getHaveShareInformationMap().get(ShareCode.ALPHA).getHaveShareLot().put(shareCertificate);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InsufficientBalanceException e) {
                e.printStackTrace();
            }
        }

        customer.start();
        log.info("{} oluştu.", customer.getCustomerName());
    }
}
