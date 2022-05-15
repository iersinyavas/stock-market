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
    private BigDecimal balance;
    private BigDecimal totalPortfolioValue;
    private BlockingQueue<ShareOrder> tradedShareOrder = new LinkedBlockingQueue<>();
    private Map<ShareCode, HaveShareInformation> haveShareInformationMap = new HashMap<>();

    @JsonIgnore
    Object lock = new Object();

    public Portfolio(){
        this.balance = BigDecimal.valueOf(SystemConstants.CUSTOMER_BALANCE);
        this.getHaveShareInformationMap().put(ShareCode.ALPHA, new HaveShareInformation(BigDecimal.valueOf(SystemConstants.START_HAVE_SHARE_LOT), Database.shareMap.get(ShareCode.ALPHA).getCurrentSellPrice()));
    }


    public void sendShareOrder(ShareOrder shareOrder) throws InterruptedException {
        shareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.SENT);
        Database.shareOrder.get(shareOrder.getShareCode()).get(shareOrder.getPrice()).get(shareOrder.getShareOrderStatus()).put(shareOrder);
    }


    public void updatePortfolioProcessShareOrder(ShareOrder shareOrder){
        synchronized (lock){
            this.updateHaveShareInformation(shareOrder);
        }
    }

    public void updateBalance(BigDecimal balance){
        synchronized (lock){
            this.setBalance(balance);
        }
    }

    public BigDecimal getBalance(){
        synchronized (lock){
            return this.balance;
        }
    }

    public void setBalance(BigDecimal balance){
        synchronized (lock){
            this.balance = balance;
        }
    }

    public void salaryPayment(BigDecimal salaryAmount){
        this.setBalance(this.getBalance().add(salaryAmount));
    }

    private void controlBalance(ShareOrder shareOrder) throws InsufficientBalanceException {
        if (this.getBalance().compareTo(shareOrder.getCost()) < 0){
            throw new InsufficientBalanceException();
        }
    }

    public ShareOrder createShareOrder(){
        ShareCode shareCode = ShareCode.values()[RandomData.shareCodeIndex()];
        Share share = Database.shareMap.get(shareCode);
        ShareOrder shareOrder = new ShareOrder(share, this.getBalance(), this.getHaveShareInformationMap().get(shareCode));
        if (shareOrder.getLot().compareTo(BigDecimal.ZERO) > 0) {
            this.updatePortfolioProcessShareOrder(shareOrder);
            return shareOrder;
        }
        return null;
    }

    public void updateHaveShareInformation(ShareOrder shareOrder){
        synchronized (lock){
            HaveShareInformation haveShareInformation = this.getHaveShareInformationMap().get(shareOrder.getShareCode());
            if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.SELL)){
                if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.CREATED)){
                    haveShareInformation.setAvailableHaveShareLot(haveShareInformation.getAvailableHaveShareLot().subtract(shareOrder.getLot()));
                }
                if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE) || shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMAINING)){
                    haveShareInformation.setHaveShareLot(haveShareInformation.getHaveShareLot().subtract(shareOrder.getLot()));
                    haveShareInformation.setTotalCost(haveShareInformation.getTotalCost().subtract(shareOrder.getCost()));
                    haveShareInformation.setAveragePrice(haveShareInformation.getTotalCost().divide(haveShareInformation.getHaveShareLot(), 2, RoundingMode.FLOOR).setScale(2, RoundingMode.FLOOR));
                    this.updateBalance(this.getBalance().add(shareOrder.getCost()));
                }
            }else {
                if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.CREATED)){
                    this.updateBalance(this.getBalance().subtract(shareOrder.getCost()));
                }
                if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE) || shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMAINING)){
                    haveShareInformation.setAvailableHaveShareLot(haveShareInformation.getAvailableHaveShareLot().add(shareOrder.getLot()));
                    haveShareInformation.setHaveShareLot(haveShareInformation.getHaveShareLot().add(shareOrder.getLot()));
                    haveShareInformation.setTotalCost(haveShareInformation.getTotalCost().add(shareOrder.getCost()));
                    haveShareInformation.setAveragePrice(haveShareInformation.getTotalCost().divide(haveShareInformation.getHaveShareLot(), 2, RoundingMode.FLOOR).setScale(2, RoundingMode.FLOOR));
                }
            }
        }

    }



}
