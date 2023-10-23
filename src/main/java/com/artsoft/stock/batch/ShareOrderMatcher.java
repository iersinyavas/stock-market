package com.artsoft.stock.batch;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.service.broker.StockMarketService;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class ShareOrderMatcher extends Thread {

    private final StockMarketService stockMarketService;
    private final BatchUtil batchUtil;

    public static boolean firstWork = true;
    public Object lock = new Object();

    @Override
    public void run() {
        try {
            synchronized (lock) {
                this.lock();
                while (true){
                    Share share = batchUtil.getShare();
                    try {
                        stockMarketService.matchShareOrder(share);
                    } catch (InsufficientBalanceException e) {
                        e.getMessage();
                    }
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
