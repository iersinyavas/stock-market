package com.artsoft.stock;

import com.artsoft.stock.model.Customer;
import com.artsoft.stock.model.Portfolio;
import com.artsoft.stock.model.Share;
import com.artsoft.stock.model.ShareOrder;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.service.ShareMarketService;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.SystemConstants;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.BlockingQueue;


@SpringBootApplication
@Slf4j
public class StockApplication implements CommandLineRunner {



    @Autowired
    private ShareMarketService shareMarketService;

    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }

    @PostConstruct
    public void init() {
        Database.shareOrder.put(ShareCode.ALPHA, new HashMap<>());
        Database.shareMap.put(ShareCode.ALPHA, new Share(ShareCode.ALPHA));
        Database.customerMap.put("A", new Customer("A", new Portfolio(), BigDecimal.valueOf(SystemConstants.CUSTOMER_SALARY)));
        Database.customerMap.put("B", new Customer("B", new Portfolio(), BigDecimal.valueOf(SystemConstants.CUSTOMER_SALARY)));
        Database.customerMap.put("C", new Customer("C", new Portfolio(), BigDecimal.valueOf(SystemConstants.CUSTOMER_SALARY)));
        Database.customerMap.put("D", new Customer("D", new Portfolio(), BigDecimal.valueOf(SystemConstants.CUSTOMER_SALARY)));
        Database.customerMap.put("E", new Customer("E", new Portfolio(), BigDecimal.valueOf(SystemConstants.CUSTOMER_SALARY)));
        Database.customerMap.put("F", new Customer("F", new Portfolio(), BigDecimal.valueOf(SystemConstants.CUSTOMER_SALARY)));
        Database.customerMap.put("G", new Customer("G", new Portfolio(), BigDecimal.valueOf(SystemConstants.CUSTOMER_SALARY)));
        Database.customerMap.put("H", new Customer("H", new Portfolio(), BigDecimal.valueOf(SystemConstants.CUSTOMER_SALARY)));
        Database.customerMap.put("I", new Customer("I", new Portfolio(), BigDecimal.valueOf(SystemConstants.CUSTOMER_SALARY)));
        Database.customerMap.put("J", new Customer("J", new Portfolio(), BigDecimal.valueOf(SystemConstants.CUSTOMER_SALARY)));
        Database.customerMap.put("K", new Customer("K", new Portfolio(), BigDecimal.valueOf(SystemConstants.CUSTOMER_SALARY)));
        Database.customerMap.put("L", new Customer("L", new Portfolio(), BigDecimal.valueOf(SystemConstants.CUSTOMER_SALARY)));
    }

    @Override
    public void run(String... args) throws Exception {
        Customer customerA = Database.customerMap.get("A");
        customerA.setName("A");

        Customer customerB = Database.customerMap.get("B");
        customerB.setName("B");

        Customer customerC = Database.customerMap.get("C");
        customerC.setName("C");

        Customer customerD = Database.customerMap.get("D");
        customerD.setName("D");

        Customer customerE = Database.customerMap.get("E");
        customerE.setName("E");

        Customer customerF = Database.customerMap.get("F");
        customerF.setName("F");

        Customer customerG = Database.customerMap.get("G");
        customerG.setName("G");

        Customer customerH = Database.customerMap.get("H");
        customerH.setName("H");

        Customer customerI = Database.customerMap.get("I");
        customerI.setName("I");

        Customer customerJ = Database.customerMap.get("J");
        customerJ.setName("J");

        Customer customerK = Database.customerMap.get("K");
        customerK.setName("K");

        Customer customerL = Database.customerMap.get("L");
        customerL.setName("L");


        Share shareA = Database.shareMap.get(ShareCode.ALPHA);
        Thread processedBuyLevelShareOrders = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(SystemConstants.SHARE_ORDER_PROCESS_SLEEP);
                    Map<ShareOrderStatus, BlockingQueue<ShareOrder>> shareOrderMap = this.selectPrice(shareA);
                    if (Objects.isNull(shareOrderMap)){
                        continue;
                    }
                    ShareOrder buyShareOrder = shareOrderMap.get(ShareOrderStatus.BUY).peek();
                    ShareOrder sellShareOrder = shareOrderMap.get(ShareOrderStatus.SELL).peek();

                    shareMarketService.processedShareOrders(buyShareOrder, sellShareOrder);

                    if (buyShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                        shareOrderMap.get(ShareOrderStatus.BUY).take();
                        log.info("Alış tarafında işlem gerçekleşti Alış: {}   Satış: {} ---> Kademede bekleyen: {}", shareA.getCurrentBuyPrice(), shareA.getCurrentSellPrice(), shareOrderMap.get(ShareOrderStatus.BUY).size());
                    }else {
                        buyShareOrder.setLot(buyShareOrder.getRemainingLot());
                        buyShareOrder.setCost(buyShareOrder.getRemainingCost());
                    }
                    if (sellShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                        shareOrderMap.get(ShareOrderStatus.SELL).take();
                        log.info("Alış tarafında işlem gerçekleşti Alış: {}   Satış: {} ---> Kademede bekleyen: {}", shareA.getCurrentBuyPrice(), shareA.getCurrentSellPrice(), shareOrderMap.get(ShareOrderStatus.BUY).size());
                    }else {
                        sellShareOrder.setLot(buyShareOrder.getRemainingLot());
                        sellShareOrder.setCost(buyShareOrder.getRemainingCost());
                    }
                } catch (InterruptedException ex) {

                }
            }
        });
        processedBuyLevelShareOrders.setName("Buy");

        Thread processedSellLevelShareOrders = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(SystemConstants.SHARE_ORDER_PROCESS_SLEEP);

                    Map<ShareOrderStatus, BlockingQueue<ShareOrder>> shareOrderMap = this.selectPrice(shareA);
                    if (Objects.isNull(shareOrderMap)){
                        continue;
                    }
                    ShareOrder buyShareOrder = shareOrderMap.get(ShareOrderStatus.BUY).peek();
                    ShareOrder sellShareOrder = shareOrderMap.get(ShareOrderStatus.SELL).peek();
                    shareMarketService.processedShareOrders(buyShareOrder, sellShareOrder);

                    if (buyShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                        shareOrderMap.get(ShareOrderStatus.BUY).take();
                        log.info("Satış tarafında işlem gerçekleşti Alış: {}   Satış: {} ----------------------------> Kademede bekleyen: {}", shareA.getCurrentBuyPrice(), shareA.getCurrentSellPrice(), shareOrderMap.get(ShareOrderStatus.SELL).size());
                    }else {
                        buyShareOrder.setLot(buyShareOrder.getRemainingLot());
                        buyShareOrder.setCost(buyShareOrder.getRemainingCost());
                    }
                    if (sellShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                        shareOrderMap.get(ShareOrderStatus.SELL).take();
                        log.info("Satış tarafında işlem gerçekleşti Alış: {}   Satış: {} ----------------------------> Kademede bekleyen: {}", shareA.getCurrentBuyPrice(), shareA.getCurrentSellPrice(), shareOrderMap.get(ShareOrderStatus.SELL).size());
                    }else {
                        sellShareOrder.setLot(buyShareOrder.getRemainingLot());
                        sellShareOrder.setCost(buyShareOrder.getRemainingCost());
                    }
                } catch (InterruptedException ex) {

                }
            }
        });
        processedSellLevelShareOrders.setName("Sell");

        customerA.start();
        customerB.start();
        customerC.start();
        customerD.start();
        customerE.start();
        customerF.start();
        customerG.start();
        customerH.start();
        customerI.start();
        customerJ.start();
        customerK.start();
        customerL.start();
        processedBuyLevelShareOrders.start();
        processedSellLevelShareOrders.start();
    }

    Object lock = new Object();

    public Map<ShareOrderStatus, BlockingQueue<ShareOrder>> selectPrice(Share share){
        BlockingQueue<ShareOrder> buyShareOrderStatusQueue;
        BlockingQueue<ShareOrder> sellShareOrderStatusQueue;
        Map<ShareOrderStatus, BlockingQueue<ShareOrder>> shareOrderMap = new HashMap<>();
        synchronized (lock){
            if (Thread.currentThread().getName().equals("Sell")){
                buyShareOrderStatusQueue = Database.shareOrder.get(share.getShareCode()).get(share.getCurrentSellPrice()).get(ShareOrderStatus.BUY);
                sellShareOrderStatusQueue = Database.shareOrder.get(share.getShareCode()).get(share.getCurrentSellPrice()).get(ShareOrderStatus.SELL);

                if (sellShareOrderStatusQueue.isEmpty()){
                    share.getSpread().setSpread(share, DirectionFlag.UP);
                    Database.shareMap.put(share.getShareCode(), share);
                    return null;
                }

                if(buyShareOrderStatusQueue.isEmpty()){
                    return null;
                }
                shareOrderMap.put(ShareOrderStatus.BUY, buyShareOrderStatusQueue);
                shareOrderMap.put(ShareOrderStatus.SELL, sellShareOrderStatusQueue);
            }else {
                buyShareOrderStatusQueue = Database.shareOrder.get(share.getShareCode()).get(share.getCurrentBuyPrice()).get(ShareOrderStatus.BUY);
                sellShareOrderStatusQueue = Database.shareOrder.get(share.getShareCode()).get(share.getCurrentBuyPrice()).get(ShareOrderStatus.SELL);

                if (buyShareOrderStatusQueue.isEmpty()){
                    share.getSpread().setSpread(share, DirectionFlag.DOWN);
                    Database.shareMap.put(share.getShareCode(), share);
                    return null;
                }

                if(sellShareOrderStatusQueue.isEmpty()){
                    return null;
                }
                shareOrderMap.put(ShareOrderStatus.BUY, buyShareOrderStatusQueue);
                shareOrderMap.put(ShareOrderStatus.SELL, sellShareOrderStatusQueue);
            }
            return shareOrderMap;

        }
    }

}
