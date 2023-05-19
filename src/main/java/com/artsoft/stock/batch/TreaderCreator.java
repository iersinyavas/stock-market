package com.artsoft.stock.batch;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.service.TraderService;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class TreaderCreator extends Thread {

    @Autowired
    private TraderService traderService;
    @Autowired
    private ShareRepository shareRepository;
    @Autowired
    private ShareOrderCreator shareOrderCreator;
    private final BatchUtil batchUtil;

    public Object lock = new Object();
    private Random random = new Random();

    @Override
    public void run() {
        synchronized (lock) {
            while (true) {
                try {
                    Thread.sleep(random.nextInt(10));
                    Share share = shareRepository.findById(batchUtil.getShareId()).get();
                    if (share.getLot().compareTo(BigDecimal.ZERO) != 0){
                        traderService.createTrader(share);
                    }else {
                        shareOrderCreator.openLock();
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void openLock(){
        synchronized (lock){
            lock.notify();
        }
    }
}
