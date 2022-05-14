package com.artsoft.stock.service;

import com.artsoft.stock.model.HaveShareInformation;
import com.artsoft.stock.model.Share;
import com.artsoft.stock.model.ShareOrder;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
@EnableScheduling
public class ShareMarketService {

    public static BlockingQueue<ShareOrderStatus> processingBuyShareOrder = new LinkedBlockingQueue<>();
    public static BlockingQueue<ShareOrderStatus> processingSellShareOrder = new LinkedBlockingQueue<>();

    Object lock = new Object();
    public void processedShareOrders(ShareOrder buyShareOrder, ShareOrder sellShareOrder) {
        synchronized (lock){
            buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.PROCESSING);
            sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.PROCESSING);

            if (buyShareOrder.getRemainingLot().compareTo(sellShareOrder.getRemainingLot()) > 0) {
                buyShareOrder.setRemainingLot(buyShareOrder.getRemainingLot().subtract(sellShareOrder.getRemainingLot()));
                sellShareOrder.setRemainingLot(BigDecimal.ZERO);

                buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMAINING);
                buyShareOrder.setRemainingCost(buyShareOrder.getCost().subtract(sellShareOrder.getCost()));
                buyShareOrder.setCost(buyShareOrder.getCost().subtract(buyShareOrder.getRemainingCost()));
                buyShareOrder.setRemainingLot(buyShareOrder.getLot().subtract(sellShareOrder.getLot()));
                buyShareOrder.setLot(buyShareOrder.getLot().subtract(buyShareOrder.getRemainingLot()));
                sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);

            } else if (buyShareOrder.getRemainingLot().compareTo(sellShareOrder.getRemainingLot()) < 0) {
                sellShareOrder.setRemainingLot(sellShareOrder.getRemainingLot().subtract(buyShareOrder.getRemainingLot()));
                buyShareOrder.setRemainingLot(BigDecimal.ZERO);

                buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);
                sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMAINING);
                sellShareOrder.setRemainingCost(sellShareOrder.getCost().subtract(buyShareOrder.getCost()));
                sellShareOrder.setCost(sellShareOrder.getCost().subtract(sellShareOrder.getRemainingCost()));
                sellShareOrder.setRemainingLot(sellShareOrder.getLot().subtract(buyShareOrder.getLot()));
                sellShareOrder.setLot(sellShareOrder.getLot().subtract(buyShareOrder.getRemainingLot()));

            } else {
                buyShareOrder.setRemainingLot(BigDecimal.ZERO);
                sellShareOrder.setRemainingLot(BigDecimal.ZERO);

                buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);
                sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);
            }
        }
    }

    @Scheduled(cron = "*/60 * * * * *")
    public void nexDaySharePrice() {
        Share share = Database.shareMap.get(ShareCode.ALPHA);
        share.updateShare();
    }

    @Scheduled(cron = "0 */2 * ? * *")
    public void customerSalaryPayment() {
        Database.customerMap.keySet().stream().forEach(s -> {
            log.info("Key: {}", s);
            Database.customerMap.get(s).salaryPayment();
        });
    }

//    @Scheduled(cron = "0 */15 * ? * *")
//    public void freeCapitalIncrease() {
//        Share share = Database.shareMap.get(ShareCode.ALPHA);
//        share.freeCapitalIncrease(null);
//        log.info("Bedelsiz sermaye artırımı");
//    }

//    @Scheduled(cron = "*/2 * * * * *")
//    public void netCost(){
//        Share share = Database.shareMap.get(ShareCode.ALPHA);
//        Database.customerMap.keySet().stream().forEach(s -> {
//            HaveShareInformation haveShareInformation = Database.customerMap.get(s).getPortfolio().getHaveShareInformationMap().get(share.getShareCode());
//            haveShareInformation.setNetCost(haveShareInformation.getTotalCost().subtract(haveShareInformation.getHaveShareLot().multiply(share.getCurrentBuyPrice())));
//        });
//    }
}
