package com.artsoft.stock;

import com.artsoft.stock.model.thread.BuyProcessShareMarket;
import com.artsoft.stock.model.Share;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.model.thread.CustomerCreator;
import com.artsoft.stock.model.thread.SellProcessShareMarket;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.service.CustomerService;
import com.artsoft.stock.util.GeneralEnumeration.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;


@SpringBootApplication
@Slf4j
public class StockApplication implements CommandLineRunner {

    public static Object lock = new Object();

    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }

    public void openLock(){
        synchronized (lock){
            lock.notify();
        }
    }

    @PostConstruct
    public void init() {
        Database.limitShareOrder.put(ShareCode.ALPHA, new HashMap<>());
        Database.marketShareOrder.put(ShareCode.ALPHA, new HashMap<>());
        Database.marketShareOrder.get(ShareCode.ALPHA).put(ShareOrderStatus.BUY, new LinkedBlockingQueue<>());
        Database.marketShareOrder.get(ShareCode.ALPHA).put(ShareOrderStatus.SELL, new LinkedBlockingQueue<>());

        Database.shareMap.put(ShareCode.ALPHA, new Share(ShareCode.ALPHA));

        Share shareA = Database.shareMap.get(ShareCode.ALPHA);
        Database.processShareOrderThread.put(ShareOrderStatus.BUY, new BuyProcessShareMarket("buyProcess", shareA));
        Database.processShareOrderThread.put(ShareOrderStatus.SELL, new SellProcessShareMarket("sellProcess", shareA));
        Database.systemThread.put("createCustomer", new CustomerCreator("createCustomer"));
    }

    @Override
    public void run(String... args) throws Exception {
        BuyProcessShareMarket buyProcess = (BuyProcessShareMarket)Database.processShareOrderThread.get(ShareOrderStatus.BUY);
        SellProcessShareMarket sellProcess = (SellProcessShareMarket)Database.processShareOrderThread.get(ShareOrderStatus.SELL);
        Thread createCustomer = Database.systemThread.get("createCustomer");

        createCustomer.start();
        buyProcess.start();
        sellProcess.start();

    }


}
