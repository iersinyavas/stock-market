package com.artsoft.stock.batch;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.service.ShareOrderService;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.service.TraderService;
import com.artsoft.stock.util.BatchUtil;
import com.artsoft.stock.util.GeneralEnumeration;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

@Component
@RequiredArgsConstructor
public class ShareOrderCreator extends Thread {

    private final ShareOrderService shareOrderService;
    private final ShareOrderMatcher shareOrderMatcher;
    private final BatchUtil batchUtil;
    private final TraderService traderService;

    public Object lock = new Object();
    private Random random = new Random();

    @Override
    public void run() {
        try {
            Share share = batchUtil.getShare();
            List<Long> doProcessTraderList = traderService.getTraderList(share);
            while (true){
                Thread.sleep(new Random().nextInt(5000));
                synchronized (lock) {
                    List<Long> traderIdList = traderService.getAllTraderIdList(share.getPrice());
                    Long traderId = traderIdList.get(random.nextInt(traderIdList.size()));
                    if (!doProcessTraderList.contains(traderId)){
                        Trader trader = traderService.createNewTrader(share);
                        trader = traderService.save(trader);
                        traderId = trader.getTraderId();
                    }
                    shareOrderService.createShareOrder(share, traderId);
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

    public void lock() throws InterruptedException {
        synchronized (lock){
            this.interrupt();
        }
    }
}
