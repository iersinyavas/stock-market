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
                    BlockingQueue<ShareOrder> buyLimitShareOrderStatusQueue = Database.limitShareOrder.get(share.getShareCode()).get(share.getCurrentSellPrice()).get(GeneralEnumeration.ShareOrderStatus.BUY);
                    BlockingQueue<ShareOrder> sellLimitShareOrderStatusQueue = Database.limitShareOrder.get(share.getShareCode()).get(share.getCurrentSellPrice()).get(GeneralEnumeration.ShareOrderStatus.SELL);

                    BlockingQueue<ShareOrder> buyMarketShareOrderQueue = Database.marketShareOrder.get(ShareCode.ALPHA).get(GeneralEnumeration.ShareOrderStatus.BUY);
                    BlockingQueue<ShareOrder> sellMarketShareOrderQueue = Database.marketShareOrder.get(ShareCode.ALPHA).get(GeneralEnumeration.ShareOrderStatus.SELL);

                    if (sellLimitShareOrderStatusQueue.isEmpty()){
                        share.getSpread().setSpread(share, GeneralEnumeration.DirectionFlag.UP);
                        Database.shareMap.put(share.getShareCode(), share);
                        continue;
                    }

                    if(buyLimitShareOrderStatusQueue.isEmpty()){
                        continue;
                    }

                    ShareOrder buyShareOrder = buyLimitShareOrderStatusQueue.peek();
                    ShareOrder sellShareOrder = sellLimitShareOrderStatusQueue.peek();

                    Customer buyCustomer = Database.customerMap.get(buyShareOrder.getCustomerName());
                    Customer sellCustomer = Database.customerMap.get(sellShareOrder.getCustomerName());

                    shareMarketService.processedShareOrders(share, buyLimitShareOrderStatusQueue, sellLimitShareOrderStatusQueue, buyShareOrder, sellShareOrder,  buyCustomer, sellCustomer);
                    log.info("Emir: {} ----- Alış: {}   Satış: {} ----- Emir: {},   Alan: {} --- Satan: {}       SATIŞ", buyLimitShareOrderStatusQueue.size(), share.getCurrentBuyPrice(), share.getCurrentSellPrice(),
                            sellLimitShareOrderStatusQueue.size(), buyCustomer.getCustomerName(), sellCustomer.getCustomerName());
                } catch (InterruptedException ex) {

                } catch (NullPointerException np){

                }
            }
        }
    }
}
