package com.artsoft.stock.batch;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.service.ShareOrderService;
import com.artsoft.stock.service.TraderService;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

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
            Boolean createTrader = Boolean.TRUE;
            Share share = batchUtil.getShare();
            while (true){
                Thread.sleep(new Random().nextInt(500));
                synchronized (lock) {
                    List<Long> traderIdList = traderService.getAllTraderIdList();
                    Long traderId = traderIdList.get(random.nextInt(traderIdList.size()));
                    if (share.getMarketBookRatio().compareTo(BigDecimal.ONE) < 0 && createTrader){
                        Trader trader = traderService.createNewTrader(share);
                        trader = traderService.save(trader);
                        traderId = trader.getTraderId();
                        createTrader = Boolean.FALSE;
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
