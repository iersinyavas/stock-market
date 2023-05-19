package com.artsoft.stock.batch;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.service.StockMarketService;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class ShareOrderMatcher extends Thread {

    private final StockMarketService stockMarketService;
    private final BatchUtil batchUtil;

    public Object lock = new Object();
    private Random random = new Random();

    @Override
    public void run() {
        try {
            synchronized (lock) {
                while (true){
                    this.lock();
                    Share share = batchUtil.getShare();
                    stockMarketService.matchShareOrder(share);
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
