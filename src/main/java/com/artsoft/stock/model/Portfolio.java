package com.artsoft.stock.model;

import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.exception.WrongLotInformationException;
import com.artsoft.stock.model.share.ShareCertificate;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.SystemConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
public class Portfolio {
    private BigDecimal balance;
    private BigDecimal totalPortfolioValue;
    private Map<ShareCode, HaveShareInformation> haveShareInformationMap = new HashMap<>();

    @JsonIgnore
    Object lock = new Object();

    public Portfolio(){
        this.balance = SystemConstants.CUSTOMER_BALANCE;
        this.getHaveShareInformationMap().put(ShareCode.ALPHA, new HaveShareInformation());
    }


    public void sendShareOrder(ShareOrder shareOrder) throws InterruptedException {
        shareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.SENT);
        if (shareOrder.getShareOrderType().equals(ShareOrderType.LIMIT)) {
            Database.limitShareOrder.get(shareOrder.getShareCode()).get(shareOrder.getPrice()).get(shareOrder.getShareOrderStatus()).put(shareOrder);
        }else {
            Database.marketShareOrder.get(shareOrder.getShareCode()).get(shareOrder.getShareOrderStatus()).put(shareOrder);
        }
    }

    public synchronized void addBalance(BigDecimal balance){
        this.setBalance(this.getBalance().add(balance));
    }

    public synchronized void subtractBalance(BigDecimal balance) throws InsufficientBalanceException {
        balance = this.getBalance().subtract(balance);
        if (balance.compareTo(BigDecimal.ZERO)<=0){
            throw new InsufficientBalanceException();
        }
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

    public ShareOrder createShareOrder(){
        synchronized (lock){
            ShareCode shareCode = ShareCode.values()[RandomData.shareCodeIndex()];
            Share share = Database.shareMap.get(shareCode);
            ShareOrder shareOrder = null;
            try {
                shareOrder = new ShareOrder(share, this.getBalance(), this.getHaveShareInformationMap().get(shareCode));

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
                    if (shareOrder.getShareOrderType().equals(ShareOrderType.LIMIT)){
                        this.subtractBalance(shareOrder.getCost());
                    }
                }
                return shareOrder;
            } catch (WrongLotInformationException e) {
                e.getMessage();
            } catch (InsufficientBalanceException e) {
                e.getMessage();
            }
            return null;
        }

    }


}
