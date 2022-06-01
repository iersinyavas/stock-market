package com.artsoft.stock.service;

import com.artsoft.stock.model.Share;
import com.artsoft.stock.model.ShareOrder;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.model.thread.BuyProcessShareMarket;
import com.artsoft.stock.model.thread.SellProcessShareMarket;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@EnableScheduling
public class ShareService {
    @Scheduled(cron = "0 */2 * ? * *")
    public void nexDaySharePrice() throws InterruptedException {

        Database.customerMap.keySet().forEach(customerName -> {
            Database.customerMap.get(customerName).setIsWait(Boolean.TRUE);
        });
        BuyProcessShareMarket buyProcess = (BuyProcessShareMarket)Database.processShareOrderThread.get(GeneralEnumeration.ShareOrderStatus.BUY);
        SellProcessShareMarket sellProcess = (SellProcessShareMarket)Database.processShareOrderThread.get(GeneralEnumeration.ShareOrderStatus.SELL);
        buyProcess.setIsWait(Boolean.TRUE);
        sellProcess.setIsWait(Boolean.TRUE);

        Thread.sleep(15000);
        Share share = Database.shareMap.get(ShareCode.ALPHA);
        for (BigDecimal price = share.getMinPrice(); price.compareTo(share.getMaxPrice())<=0; price = price.add(BigDecimal.valueOf(0.01))){
            while (!Database.shareOrder.get(share.getShareCode()).get(price).get(GeneralEnumeration.ShareOrderStatus.BUY).isEmpty()){
                ShareOrder shareOrder = Database.shareOrder.get(share.getShareCode()).get(price).get(GeneralEnumeration.ShareOrderStatus.BUY).take();
                Database.customerMap.get(shareOrder.getCustomerName()).getPortfolio().updatePortfolioNotProcessedShareOrder(shareOrder);
            }

            while (!Database.shareOrder.get(share.getShareCode()).get(price).get(GeneralEnumeration.ShareOrderStatus.SELL).isEmpty()){
                ShareOrder shareOrder = Database.shareOrder.get(share.getShareCode()).get(price).get(GeneralEnumeration.ShareOrderStatus.SELL).take();
                Database.customerMap.get(shareOrder.getCustomerName()).getPortfolio().updatePortfolioNotProcessedShareOrder(shareOrder);
            }
        }
        share.updateShare();
        Thread.sleep(5000);

        buyProcess.openLock();
        sellProcess.openLock();
        for (String customerName : Database.customerMap.keySet()){
            Database.customerMap.get(customerName).openLock();
        }

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
