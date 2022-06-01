package com.artsoft.stock.service;

import com.artsoft.stock.model.*;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.model.thread.BuyProcessShareMarket;
import com.artsoft.stock.model.thread.SellProcessShareMarket;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;


@Slf4j
public class ShareMarketService {

    Object lock = new Object();

    public synchronized void processedShareOrders(Share share, BlockingQueue<ShareOrder> buyShareOrderStatusQueue, BlockingQueue<ShareOrder> sellShareOrderStatusQueue, ShareOrder buyShareOrder, ShareOrder sellShareOrder) throws InterruptedException {
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

            List<ShareOrder> shareOrderList = Arrays.asList(buyShareOrder, sellShareOrder);
            this.updatePortfolioProcessShareOrder(shareOrderList);
            if (buyShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                buyShareOrderStatusQueue.take();
            }else {
                buyShareOrder.setLot(buyShareOrder.getRemainingLot());
                buyShareOrder.setCost(buyShareOrder.getRemainingCost());
            }
            if (sellShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                sellShareOrderStatusQueue.take();
            }else {
                sellShareOrder.setLot(buyShareOrder.getRemainingLot());
                sellShareOrder.setCost(buyShareOrder.getRemainingCost());
            }
            log.info("Emir: {} ----- Alış: {}   Satış: {} ----- Emir: {}", buyShareOrderStatusQueue.size(), share.getCurrentBuyPrice(), share.getCurrentSellPrice(), sellShareOrderStatusQueue.size());

        } else if (buyShareOrder.getRemainingLot().compareTo(sellShareOrder.getRemainingLot()) < 0) {
            sellShareOrder.setRemainingLot(sellShareOrder.getRemainingLot().subtract(buyShareOrder.getRemainingLot()));
            buyShareOrder.setRemainingLot(BigDecimal.ZERO);

            buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);
            sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMAINING);
            sellShareOrder.setRemainingCost(sellShareOrder.getCost().subtract(buyShareOrder.getCost()));
            sellShareOrder.setCost(sellShareOrder.getCost().subtract(sellShareOrder.getRemainingCost()));
            sellShareOrder.setRemainingLot(sellShareOrder.getLot().subtract(buyShareOrder.getLot()));
            sellShareOrder.setLot(sellShareOrder.getLot().subtract(sellShareOrder.getRemainingLot()));

            List<ShareOrder> shareOrderList = Arrays.asList(buyShareOrder, sellShareOrder);
            this.updatePortfolioProcessShareOrder(shareOrderList);
            if (buyShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                buyShareOrderStatusQueue.take();
            }else {
                buyShareOrder.setLot(buyShareOrder.getRemainingLot());
                buyShareOrder.setCost(buyShareOrder.getRemainingCost());
            }
            if (sellShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                sellShareOrderStatusQueue.take();
            }else {
                sellShareOrder.setLot(buyShareOrder.getRemainingLot());
                sellShareOrder.setCost(buyShareOrder.getRemainingCost());
            }
            log.info("Emir: {} ----- Alış: {}   Satış: {} ----- Emir: {}", buyShareOrderStatusQueue.size(), share.getCurrentBuyPrice(), share.getCurrentSellPrice(), sellShareOrderStatusQueue.size());

        } else {
            buyShareOrder.setRemainingLot(BigDecimal.ZERO);
            sellShareOrder.setRemainingLot(BigDecimal.ZERO);

            buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);
            sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);

            List<ShareOrder> shareOrderList = Arrays.asList(buyShareOrder, sellShareOrder);
            this.updatePortfolioProcessShareOrder(shareOrderList);
            if (buyShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                buyShareOrderStatusQueue.take();
            }
            if (sellShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                sellShareOrderStatusQueue.take();
            }
            log.info("Emir: {} ----- Alış: {}   Satış: {} ----- Emir: {}", buyShareOrderStatusQueue.size(), share.getCurrentBuyPrice(), share.getCurrentSellPrice(), sellShareOrderStatusQueue.size());
        }
    }

    public synchronized void updatePortfolioProcessShareOrder(List<ShareOrder> shareOrderList){
        for (ShareOrder shareOrder : shareOrderList){
            Portfolio portfolio = Database.customerMap.get(shareOrder.getCustomerName()).getPortfolio();
            HaveShareInformation haveShareInformation = portfolio.getHaveShareInformationMap().get(shareOrder.getShareCode());
            if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.SELL)){
                this.updatePortfolioForSellProcess(shareOrder, portfolio, haveShareInformation);
            }else {
                this.updatePortfolioForBuyProcess(shareOrder, portfolio, haveShareInformation);
            }
        }

    }

    private void updatePortfolioForBuyProcess(ShareOrder shareOrder, Portfolio portfolio, HaveShareInformation haveShareInformation) {
        if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE) || shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMAINING)){
            haveShareInformation.setAvailableHaveShareLot(haveShareInformation.getAvailableHaveShareLot().add(shareOrder.getLot()));
            haveShareInformation.setHaveShareLot(haveShareInformation.getHaveShareLot().add(shareOrder.getLot()));
            /*haveShareInformation.setTotalCost(haveShareInformation.getTotalCost().add(shareOrder.getCost()));
            if (haveShareInformation.getHaveShareLot().compareTo(BigDecimal.ZERO)>0){
                haveShareInformation.setAveragePrice(haveShareInformation.getTotalCost().divide(haveShareInformation.getHaveShareLot(), 2, RoundingMode.FLOOR).setScale(2, RoundingMode.FLOOR));
            }else {
                haveShareInformation.setAveragePrice(BigDecimal.ZERO);
            }*/
            portfolio.getHaveShareInformationMap().put(shareOrder.getShareCode(), haveShareInformation);
        }
    }

    private void updatePortfolioForSellProcess(ShareOrder shareOrder, Portfolio portfolio, HaveShareInformation haveShareInformation) {
        if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE) || shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMAINING)){
            haveShareInformation.setHaveShareLot(haveShareInformation.getHaveShareLot().subtract(shareOrder.getLot()));
            /*haveShareInformation.setTotalCost(haveShareInformation.getTotalCost().subtract(shareOrder.getCost()));
            if (haveShareInformation.getHaveShareLot().compareTo(BigDecimal.ZERO)>0){
                haveShareInformation.setAveragePrice(haveShareInformation.getTotalCost().divide(haveShareInformation.getHaveShareLot(), 2, RoundingMode.FLOOR).setScale(2, RoundingMode.FLOOR));
            }else {
                haveShareInformation.setAveragePrice(BigDecimal.ZERO);
            }*/

            portfolio.updateBalance(portfolio.getBalance().add(shareOrder.getCost()));
            portfolio.getHaveShareInformationMap().put(shareOrder.getShareCode(), haveShareInformation);
        }
    }


}
