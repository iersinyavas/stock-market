package com.artsoft.stock.model.thread;

import com.artsoft.stock.model.Share;
import com.artsoft.stock.model.ShareOrder;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.service.ShareMarketService;
import com.artsoft.stock.util.GeneralEnumeration;
import com.artsoft.stock.util.SystemConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Slf4j
public class SellProcessShareMarket extends Thread{

    private ShareMarketService shareMarketService = new ShareMarketService();

    private Share share;
    public Boolean isWait = Boolean.FALSE;
    public Object lock = new Object();

    public SellProcessShareMarket(String name, Share share) {
        super(name);
        this.share = share;
    }

    public void openLock(){
        synchronized (lock){
            lock.notify();
        }
    }

    @Autowired
    public SellProcessShareMarket(ShareMarketService shareMarketService) {
        this.shareMarketService = shareMarketService;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lock){
                try {
                    Thread.sleep(SystemConstants.SHARE_ORDER_PROCESS_SLEEP);
                    if (isWait){
                        log.info("Thread: {}", Thread.currentThread().getName());
                        lock.wait();
                        isWait = Boolean.FALSE;
                    }
                    BlockingQueue<ShareOrder> buyShareOrderStatusQueue = Database.shareOrder.get(share.getShareCode()).get(share.getCurrentSellPrice()).get(GeneralEnumeration.ShareOrderStatus.BUY);
                    BlockingQueue<ShareOrder> sellShareOrderStatusQueue = Database.shareOrder.get(share.getShareCode()).get(share.getCurrentSellPrice()).get(GeneralEnumeration.ShareOrderStatus.SELL);

                    if (sellShareOrderStatusQueue.isEmpty()){
                        share.getSpread().setSpread(share, GeneralEnumeration.DirectionFlag.UP);
                        Database.shareMap.put(share.getShareCode(), share);
                        continue;
                    }

                    if(buyShareOrderStatusQueue.isEmpty()){
                        continue;
                    }

                    ShareOrder buyShareOrder = buyShareOrderStatusQueue.peek();
                    ShareOrder sellShareOrder = sellShareOrderStatusQueue.peek();

                    shareMarketService.processedShareOrders(share, buyShareOrderStatusQueue, sellShareOrderStatusQueue, buyShareOrder, sellShareOrder);
                } catch (InterruptedException ex) {

                } catch (NullPointerException np){

                }
            }
        }
    }
}
