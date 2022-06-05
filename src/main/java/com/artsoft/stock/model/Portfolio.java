package com.artsoft.stock.model;

import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.model.share.ShareCertificate;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.SystemConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Portfolio {
    private BigDecimal balance;
    private BigDecimal totalPortfolioValue;
    private Map<ShareCode, HaveShareInformation> haveShareInformationMap = new HashMap<>();

    @JsonIgnore
    Object lock = new Object();

    public Portfolio(){
        this.balance = SystemConstants.CUSTOMER_BALANCE;
        this.getHaveShareInformationMap().put(ShareCode.ALPHA, new HaveShareInformation(Database.shareMap.get(ShareCode.ALPHA).getCurrentBuyPrice().setScale(2)));
    }


    public void sendShareOrder(ShareOrder shareOrder) throws InterruptedException {
        shareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.SENT);
        Database.shareOrder.get(shareOrder.getShareCode()).get(shareOrder.getPrice()).get(shareOrder.getShareOrderStatus()).put(shareOrder);
    }

    public synchronized void addBalance(BigDecimal balance){
        this.setBalance(this.getBalance().add(balance));
    }

    public synchronized void subtractBalance(BigDecimal balance){
        this.setBalance(this.getBalance().subtract(balance));
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

    public ShareOrder createShareOrder(){
        synchronized (lock){
            ShareCode shareCode = ShareCode.values()[RandomData.shareCodeIndex()];
            Share share = Database.shareMap.get(shareCode);
            ShareOrder shareOrder = new ShareOrder(share, this.getBalance(), this.getHaveShareInformationMap().get(shareCode));
            if (shareOrder.getTempLot().compareTo(BigDecimal.ZERO) > 0) {
                BlockingQueue<ShareCertificate> lot = this.getHaveShareInformationMap().get(shareOrder.getShareCode()).getHaveShareLot();
                if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.SELL)){
                    while (!lot.isEmpty() && shareOrder.getLot().remainingCapacity() != 0){
                        try {
                            shareOrder.getLot().put(lot.take());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    this.subtractBalance(shareOrder.getCost());
                }
                return shareOrder;
            }
            return null;
        }
    }

}
