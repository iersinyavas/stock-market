package com.artsoft.stock.batch;

import com.artsoft.stock.service.StockMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ShareOrderMatcher extends Thread {

    @Autowired
    private StockMarketService stockMarketService;
    @Autowired
    private TreaderCreator treaderCreator;
    public Object lock = new Object();
    private Random random = new Random();
    private Boolean isWork = Boolean.FALSE;

    @Override
    public void run() {
        try {
            synchronized (lock) {
                lock.wait();
                Thread.sleep(random.nextInt(5000));
                while (true){
                    stockMarketService.matchShareOrderForOpenSession();
                    Thread.sleep(random.nextInt(1000));
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
