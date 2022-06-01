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
        Database.customerMap.put("A", new Customer("A", new Portfolio("A"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("B", new Customer("B", new Portfolio("B"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("C", new Customer("C", new Portfolio("C"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("D", new Customer("D", new Portfolio("D"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("E", new Customer("E", new Portfolio("E"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("F", new Customer("F", new Portfolio("F"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("G", new Customer("G", new Portfolio("G"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("H", new Customer("H", new Portfolio("H"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("I", new Customer("I", new Portfolio("I"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("J", new Customer("J", new Portfolio("J"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("K", new Customer("K", new Portfolio("K"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("L", new Customer("L", new Portfolio("L"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("M", new Customer("M", new Portfolio("M"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("N", new Customer("N", new Portfolio("N"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("O", new Customer("O", new Portfolio("O"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("P", new Customer("P", new Portfolio("P"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("R", new Customer("R", new Portfolio("R"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("S", new Customer("S", new Portfolio("S"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("T", new Customer("T", new Portfolio("T"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("U", new Customer("U", new Portfolio("U"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("V", new Customer("V", new Portfolio("V"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("Y", new Customer("Y", new Portfolio("Y"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("Z", new Customer("Z", new Portfolio("Z"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("Q", new Customer("Q", new Portfolio("Q"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("W", new Customer("W", new Portfolio("W"), SystemConstants.CUSTOMER_SALARY));
        Database.customerMap.put("X", new Customer("X", new Portfolio("X"), SystemConstants.CUSTOMER_SALARY));

        Share shareA = Database.shareMap.get(ShareCode.ALPHA);
        Database.processShareOrderThread.put(ShareOrderStatus.BUY, new BuyProcessShareMarket("buyProcess", shareA));
        Database.processShareOrderThread.put(ShareOrderStatus.SELL, new SellProcessShareMarket("sellProcess", shareA));
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

        Customer customerM = Database.customerMap.get("M");
        customerM.setName("M");

        Customer customerN = Database.customerMap.get("N");
        customerN.setName("N");

        Customer customerO = Database.customerMap.get("O");
        customerO.setName("O");

        Customer customerP = Database.customerMap.get("P");
        customerP.setName("P");

        Customer customerR = Database.customerMap.get("R");
        customerR.setName("R");

        Customer customerS = Database.customerMap.get("S");
        customerS.setName("S");

        Customer customerT = Database.customerMap.get("T");
        customerT.setName("T");

        Customer customerU = Database.customerMap.get("U");
        customerU.setName("U");

        Customer customerV = Database.customerMap.get("V");
        customerV.setName("V");

        Customer customerY = Database.customerMap.get("Y");
        customerY.setName("Y");

        Customer customerZ = Database.customerMap.get("Z");
        customerZ.setName("Z");

        Customer customerQ = Database.customerMap.get("Q");
        customerQ.setName("Q");

        Customer customerW = Database.customerMap.get("W");
        customerW.setName("W");

        Customer customerX = Database.customerMap.get("X");
        customerX.setName("X");

        BuyProcessShareMarket buyProcess = (BuyProcessShareMarket)Database.processShareOrderThread.get(ShareOrderStatus.BUY);
        SellProcessShareMarket sellProcess = (SellProcessShareMarket)Database.processShareOrderThread.get(ShareOrderStatus.SELL);


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
        customerM.start();
        customerN.start();
        customerO.start();
        customerP.start();
        customerR.start();
        customerS.start();
        customerT.start();
        customerU.start();
        customerV.start();
        customerY.start();
        customerZ.start();
        customerQ.start();
        customerW.start();
        customerX.start();
        buyProcess.start();
        sellProcess.start();
    }


}
