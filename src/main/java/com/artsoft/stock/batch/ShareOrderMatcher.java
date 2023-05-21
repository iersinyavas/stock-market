package com.artsoft.stock.batch;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.service.StockMarketService;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class ShareOrderMatcher extends Thread {

    private final StockMarketService stockMarketService;
    private final BatchUtil batchUtil;

    /*private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();*/

    public Object lock = new Object();
    private Random random = new Random();

    @Override
    public void run() {
        try {
            synchronized (lock) {
                this.lock();
                while (true){
                    Share share = batchUtil.getShare();
                    stockMarketService.matchShareOrder(share);
                    this.lock();
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

    public void lock() throws InterruptedException {
        synchronized (lock){
            lock.wait();
        }
    }
}
