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

    private Random random = new Random();

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
    }

    @Override
    public void run(String... args) throws Exception {
        Thread customerA = new Thread(() -> {
            Customer customer = Database.customerMap.get("A");

            while (true) {
                try {
                    Thread.sleep(random.nextInt(SystemConstants.CUSTOMER_RANDOM_SLEEP));
                    ShareOrder shareOrder = customer.getPortfolio().createShareOrder();
                    if (Objects.nonNull(shareOrder)){
                        customer.getPortfolio().sendShareOrder(shareOrder);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        customerA.setName("A");

        Thread customerB = new Thread(() -> {
            Customer customer = Database.customerMap.get("B");

            while (true) {
                try {
                    Thread.sleep(random.nextInt(SystemConstants.CUSTOMER_RANDOM_SLEEP));
                    ShareOrder shareOrder = customer.getPortfolio().createShareOrder();
                    if (Objects.nonNull(shareOrder)){
                        customer.getPortfolio().sendShareOrder(shareOrder);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        customerB.setName("B");

        Share shareA = Database.shareMap.get(ShareCode.ALPHA);
        Thread processedBuyLevelShareOrders = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(SystemConstants.SHARE_ORDER_PROCESS_SLEEP);
                    BlockingQueue<ShareOrder> buyShareOrderStatusQueue = Database.shareOrder.get(ShareCode.ALPHA).get(shareA.getCurrentBuyPrice()).get(ShareOrderStatus.BUY);
                    BlockingQueue<ShareOrder> sellShareOrderStatusQueue = Database.shareOrder.get(ShareCode.ALPHA).get(shareA.getCurrentBuyPrice()).get(ShareOrderStatus.SELL);

                    if (buyShareOrderStatusQueue.isEmpty()){
                        shareA.getSpread().setSpread(shareA, DirectionFlag.DOWN);
                        Database.shareMap.put(ShareCode.ALPHA, shareA);
                        continue;
                    }

                    if(sellShareOrderStatusQueue.isEmpty()){
                        continue;
                    }

                    ShareOrder buyShareOrder = buyShareOrderStatusQueue.peek();
                    ShareOrder sellShareOrder = sellShareOrderStatusQueue.peek();
                    shareMarketService.processedShareOrders(buyShareOrder, sellShareOrder);

                    if (buyShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                        buyShareOrderStatusQueue.take();
                        Database.customerMap.get(buyShareOrder.getCustomerName()).getPortfolio().updatePortfolioProcessShareOrder(buyShareOrder);
                        log.info("Alış işlemi gerçekleşti Alış: {}   Satış: {} ", shareA.getCurrentBuyPrice(), shareA.getCurrentSellPrice());
                    }
                    if (sellShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                        sellShareOrderStatusQueue.take();
                        Database.customerMap.get(buyShareOrder.getCustomerName()).getPortfolio().updatePortfolioProcessShareOrder(sellShareOrder);
                        log.info("Alış işlemi gerçekleşti Alış: {}   Satış: {} ", shareA.getCurrentBuyPrice(), shareA.getCurrentSellPrice());
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
                    BlockingQueue<ShareOrder> buyShareOrderStatusQueue = Database.shareOrder.get(ShareCode.ALPHA).get(shareA.getCurrentSellPrice()).get(ShareOrderStatus.BUY);
                    BlockingQueue<ShareOrder> sellShareOrderStatusQueue = Database.shareOrder.get(ShareCode.ALPHA).get(shareA.getCurrentSellPrice()).get(ShareOrderStatus.SELL);

                    if (sellShareOrderStatusQueue.isEmpty()){
                        shareA.getSpread().setSpread(shareA, DirectionFlag.UP);
                        Database.shareMap.put(ShareCode.ALPHA, shareA);
                        continue;
                    }

                    if(buyShareOrderStatusQueue.isEmpty()){
                        continue;
                    }

                    ShareOrder buyShareOrder = buyShareOrderStatusQueue.peek();
                    ShareOrder sellShareOrder = sellShareOrderStatusQueue.peek();
                    shareMarketService.processedShareOrders(buyShareOrder, sellShareOrder);

                    if (buyShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                        buyShareOrderStatusQueue.take();
                        Database.customerMap.get(buyShareOrder.getCustomerName()).getPortfolio().updatePortfolioProcessShareOrder(buyShareOrder);
                        log.info("Satış işlemi gerçekleşti Alış: {}   Satış: {} ", shareA.getCurrentBuyPrice(), shareA.getCurrentSellPrice());
                    }
                    if (sellShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                        sellShareOrderStatusQueue.take();
                        Database.customerMap.get(buyShareOrder.getCustomerName()).getPortfolio().updatePortfolioProcessShareOrder(sellShareOrder);
                        log.info("Satış işlemi gerçekleşti Alış: {}   Satış: {} ", shareA.getCurrentBuyPrice(), shareA.getCurrentSellPrice());
                    }
                } catch (InterruptedException ex) {

                }
            }
        });
        processedSellLevelShareOrders.setName("Sell");

        customerA.start();
        //customerB.start();
        processedBuyLevelShareOrders.start();
        processedSellLevelShareOrders.start();
    }

}
