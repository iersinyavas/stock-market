package com.artsoft.stock;

import com.artsoft.stock.model.thread.BuyProcessShareMarket;
import com.artsoft.stock.model.thread.Customer;
import com.artsoft.stock.model.Portfolio;
import com.artsoft.stock.model.Share;
import com.artsoft.stock.model.ShareOrder;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.model.thread.SellProcessShareMarket;
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
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;


@SpringBootApplication
@Slf4j
public class StockApplication implements CommandLineRunner {

    public static Object lock = new Object();
    public Boolean isWait = Boolean.FALSE;

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
        Database.shareOrder.put(ShareCode.ALPHA, new HashMap<>());
        Database.shareMap.put(ShareCode.ALPHA, new Share(ShareCode.ALPHA));

        Share shareA = Database.shareMap.get(ShareCode.ALPHA);
        Database.processShareOrderThread.put(ShareOrderStatus.BUY, new BuyProcessShareMarket("buyProcess", shareA));
        Database.processShareOrderThread.put(ShareOrderStatus.SELL, new SellProcessShareMarket("sellProcess", shareA));
    }

    @Override
    public void run(String... args) throws Exception {
        BuyProcessShareMarket buyProcess = (BuyProcessShareMarket)Database.processShareOrderThread.get(ShareOrderStatus.BUY);
        SellProcessShareMarket sellProcess = (SellProcessShareMarket)Database.processShareOrderThread.get(ShareOrderStatus.SELL);

        buyProcess.start();
        sellProcess.start();
    }


}
