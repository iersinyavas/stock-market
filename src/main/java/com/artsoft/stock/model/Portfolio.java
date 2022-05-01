package com.artsoft.stock.model;

import com.artsoft.stock.model.share.ShareCode;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Portfolio {
    private BigDecimal balance;
    private Map<ShareCode, Integer> haveShare = new HashMap<>();
    private BlockingQueue<ShareOrder> readySendShareOrder = new LinkedBlockingQueue();
    private Map<ShareCode, BigDecimal> averageCost = new HashMap<>();

    private void sendShareOrder(ShareOrder shareOrder) throws InterruptedException {
        readySendShareOrder.put(shareOrder);
    }

    public void updateBalance(ShareOrder shareOrder){
        this.setBalance(this.getBalance().subtract(shareOrder.getAmount()));
    }

    private Boolean controlBalance(ShareOrder shareOrder){
        if (this.getBalance().compareTo(shareOrder.getAmount()) > 0){
            this.setBalance(balance.subtract(shareOrder.getAmount()));
        }
    }

    public ShareOrder createShareOrder(){
        new ShareOrder(this.getBalance(), )
    }

    public void updateAverageCost(){

    }

}
