package com.artsoft.stock.batch;

import com.artsoft.stock.service.ShareOrderService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Random;

@Component
public class ShareOrderCreator extends Thread {

    @Autowired
    private ShareOrderService shareOrderService;
    @Autowired
    private ShareOrderMatcher shareOrderMatcher;
    public Object lock = new Object();
    private Random random = new Random();

    @Override
    public void run() {
        try {
            synchronized (lock) {

                while (true) {
                    lock.wait();
                    Thread.sleep(random.nextInt(5000));
                    shareOrderService.createShareOrderForOpenSession();
                    shareOrderMatcher.openLock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void openLock(){
        synchronized (lock){
            lock.notify();
        }
    }
}
