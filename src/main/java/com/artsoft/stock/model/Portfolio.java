package com.artsoft.stock.model;

import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.SystemConstants;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Portfolio {
    private BigDecimal balance;
    private BlockingQueue<ShareOrder> tradedShareOrder = new LinkedBlockingQueue<>();
    private Map<ShareCode, HaveShareInformation> haveShareInformationMap = new HashMap<>();
    Object lock = new Object();

    public Portfolio(){
        this.balance = BigDecimal.valueOf(SystemConstants.CUSTOMER_BALANCE);
        haveShareInformationMap.put(ShareCode.ALPHA, new HaveShareInformation(BigDecimal.valueOf(SystemConstants.START_HAVE_SHARE_LOT), Database.shareMap.get(ShareCode.ALPHA).getCurrentBuyPrice()));
    }


    public void sendShareOrder(ShareOrder shareOrder) throws InterruptedException {
        shareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.SENT);
        Database.shareOrder.get(shareOrder.getShareCode()).get(shareOrder.getPrice()).get(shareOrder.getShareOrderStatus()).put(shareOrder);
    }


    public void updatePortfolioProcessShareOrder(ShareOrder shareOrder){
        this.updateBalanceProcessShareOrder(shareOrder);
        this.updateLotProcessShareOrder(shareOrder);
    }

    public void updateLotProcessShareOrder(ShareOrder shareOrder){
        this.updateHaveShareInformation(shareOrder, this.getHaveShareInformationMap().get(shareOrder.getShareCode()));
    }

    private void updateBalanceProcessShareOrder(ShareOrder shareOrder){
        synchronized (lock) {
            if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.SELL) && !shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.CREATED)) {
                this.updateBalance(this.getBalance().add(shareOrder.getPrice()));
            }else {
                this.updateBalance(this.getBalance().subtract(shareOrder.getCost()));
            }
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
        HaveShareInformation haveShareInformation = this.haveShareInformationMap.get(shareCode);
        ShareOrder shareOrder = new ShareOrder(share, this.getBalance(), haveShareInformation);
        if (shareOrder.getLot().compareTo(BigDecimal.ZERO) != 0) {
            this.updatePortfolioProcessShareOrder(shareOrder);
            return shareOrder;
        }
        return null;
    }

    public void updateHaveShareInformation(ShareOrder shareOrder, HaveShareInformation haveShareInformation){
        synchronized (lock){
            if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.SELL)){
                if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.CREATED)){
                    haveShareInformation.setTotalCost(haveShareInformation.getTotalCost().subtract(shareOrder.getCost()));
                    haveShareInformation.setAvailableHaveShareLot(haveShareInformation.getAvailableHaveShareLot().subtract(shareOrder.getLot()));
                    haveShareInformation.setAverageCost(haveShareInformation.getTotalCost().divide(haveShareInformation.getHaveShareLot(), 2, RoundingMode.FLOOR).setScale(2, RoundingMode.FLOOR));
                }
                if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)){
                    haveShareInformation.setHaveShareLot(haveShareInformation.getHaveShareLot().subtract(shareOrder.getLot()));
                }
                this.getHaveShareInformationMap().put(shareOrder.getShareCode(), haveShareInformation);
            }else {
                if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)){
                    haveShareInformation.setTotalCost(haveShareInformation.getTotalCost().add(shareOrder.getCost()));
                    haveShareInformation.setAvailableHaveShareLot(haveShareInformation.getAvailableHaveShareLot().subtract(shareOrder.getLot()));
                    haveShareInformation.setHaveShareLot(haveShareInformation.getHaveShareLot().add(shareOrder.getLot()));
                    haveShareInformation.setAverageCost(haveShareInformation.getTotalCost().divide(haveShareInformation.getHaveShareLot(), 2, RoundingMode.FLOOR).setScale(2, RoundingMode.FLOOR));
                }
                this.getHaveShareInformationMap().put(shareOrder.getShareCode(), haveShareInformation);
            }
        }

    }

}
