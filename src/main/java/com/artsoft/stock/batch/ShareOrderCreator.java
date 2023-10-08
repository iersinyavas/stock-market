package com.artsoft.stock.batch;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.service.operation.ShareOrderService;
import com.artsoft.stock.service.TraderService;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public static boolean passive = false;
    public static boolean firstWork = true;
    private Random random = new Random();

    @Override
    public void run() {
        try {
            Share share = batchUtil.getShare();
            while (true){
                if (passive){
                    this.passive();
                    share = batchUtil.getShare();
                }
                Thread.sleep(new Random().nextInt(500));
                synchronized (lock) {
                    List<Long> traderIdList = traderService.getTraderIdListForShareOrder(share.getPriceStep().getPrice());
                    Long traderId = traderIdList.get(random.nextInt(traderIdList.size()));
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
            passive = true;//this.interrupt();
        }
    }

    public void passive() throws InterruptedException {
        synchronized (lock){
            lock.wait();
        }
    }
}
