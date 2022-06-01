package com.artsoft.stock.model;

import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.SystemConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Portfolio {
    private String customerName;
    private BigDecimal balance;
    private BigDecimal totalPortfolioValue;
    private Map<ShareCode, HaveShareInformation> haveShareInformationMap = new HashMap<>();

    @JsonIgnore
    Object lock = new Object();

    public Portfolio(String customerName){
        this.customerName = customerName;
        this.balance = SystemConstants.CUSTOMER_BALANCE;
        this.getHaveShareInformationMap().put(ShareCode.ALPHA, new HaveShareInformation(SystemConstants.START_HAVE_SHARE_LOT, Database.shareMap.get(ShareCode.ALPHA).getCurrentBuyPrice().setScale(2)));
    }


    public void sendShareOrder(ShareOrder shareOrder) throws InterruptedException {
        shareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.SENT);
       // this.getTradedShareOrder().put(shareOrder);
        Database.shareOrder.get(shareOrder.getShareCode()).get(shareOrder.getPrice()).get(shareOrder.getShareOrderStatus()).put(shareOrder);
    }

    public synchronized void updateBalance(BigDecimal balance){
        this.setBalance(balance);
    }

    public synchronized BigDecimal getBalance(){
        return this.balance;
    }

    public synchronized void setBalance(BigDecimal balance){
        this.balance = balance;
    }

    public void salaryPayment(BigDecimal salaryAmount){
        this.setBalance(this.getBalance().add(salaryAmount));
    }

    private void controlBalance(ShareOrder shareOrder) throws InsufficientBalanceException {
        if (this.getBalance().compareTo(shareOrder.getCost()) < 0){
            throw new InsufficientBalanceException();
        }
    }

    public synchronized Map<ShareCode, HaveShareInformation> getHaveShareInformationMap() {
        return haveShareInformationMap;
    }

    public synchronized void setHaveShareInformationMap(Map<ShareCode, HaveShareInformation> haveShareInformationMap) {
        this.haveShareInformationMap = haveShareInformationMap;
    }

    public ShareOrder createShareOrder(){
        synchronized (lock){
            ShareCode shareCode = ShareCode.values()[RandomData.shareCodeIndex()];
            Share share = Database.shareMap.get(shareCode);
            ShareOrder shareOrder = new ShareOrder(share, this.getBalance(), this.getHaveShareInformationMap().get(shareCode));
            if (shareOrder.getLot().compareTo(BigDecimal.ZERO) > 0) {
                this.updatePortfolioBeforeProcessShareOrder(shareOrder);
                return shareOrder;
            }
            return null;
        }
    }


    public void updatePortfolioBeforeProcessShareOrder(ShareOrder shareOrder){
        if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.SELL)) {
            if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.CREATED)) {
                this.getHaveShareInformationMap().get(shareOrder.getShareCode()).setAvailableHaveShareLot(this.getHaveShareInformationMap().get(shareOrder.getShareCode())
                        .getAvailableHaveShareLot().subtract(shareOrder.getLot()));
            }
        }else {
            if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.CREATED)){
                this.updateBalance(this.getBalance().subtract(shareOrder.getCost()));
            }
        }
    }

    public void updatePortfolioNotProcessedShareOrder(ShareOrder shareOrder){
        if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.SELL)) {
            if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.SENT) || shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMAINING)) {
                this.getHaveShareInformationMap().get(shareOrder.getShareCode()).setAvailableHaveShareLot(this.getHaveShareInformationMap().get(shareOrder.getShareCode())
                        .getAvailableHaveShareLot().add(shareOrder.getLot()));
            }
        }else {
            if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.SENT) || shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMAINING)){
                this.updateBalance(this.getBalance().add(shareOrder.getCost()));
            }
        }
    }

}
