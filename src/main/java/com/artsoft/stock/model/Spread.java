package com.artsoft.stock.model;

import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Spread {

    private BigDecimal buyPrice;
    private BigDecimal sellPrice;

    public Spread(Share share){
        this.buyPrice = share.getCurrentBuyPrice();
        this.sellPrice = share.getCurrentSellPrice();
        this.createSpread(share);
    }

    Object lock = new Object();
    public void setSpread(Share share, DirectionFlag directionFlag){
        synchronized (lock){
            if (directionFlag.equals(DirectionFlag.DOWN)){
                if (this.getBuyPrice().compareTo(share.getMinPrice()) == 0){
                    this.setBuyPrice(share.getMinPrice());
                    this.setSellPrice(share.getMinPrice().add(BigDecimal.valueOf(0.01)));
                    share.setCurrentBuyPrice(this.getBuyPrice());
                    share.setCurrentSellPrice(this.getSellPrice());
                    return;
                }
                this.setBuyPrice(this.getBuyPrice().subtract(BigDecimal.valueOf(0.01)));
                this.setSellPrice(this.getSellPrice().subtract(BigDecimal.valueOf(0.01)));
                share.setCurrentBuyPrice(this.getBuyPrice());
                share.setCurrentSellPrice(this.getSellPrice());
                return;
            }else {
                if (this.getSellPrice().compareTo(share.getMaxPrice()) == 0) {
                    this.setSellPrice(share.getMaxPrice());
                    this.setBuyPrice(share.getMaxPrice().subtract(BigDecimal.valueOf(0.01)));
                    share.setCurrentSellPrice(this.getSellPrice());
                    share.setCurrentBuyPrice(this.getBuyPrice());
                    return;
                }
                this.setSellPrice(this.getSellPrice().add(BigDecimal.valueOf(0.01)));
                this.setBuyPrice(this.getBuyPrice().add(BigDecimal.valueOf(0.01)));
                share.setCurrentSellPrice(this.getSellPrice());
                share.setCurrentBuyPrice(this.getBuyPrice());
                return;
            }
        }
    }

    public Spread createSpread(Share share){
        BigDecimal min = share.getMinPrice().multiply(BigDecimal.valueOf(100));
        BigDecimal max = share.getMaxPrice().multiply(BigDecimal.valueOf(100));
        for (int i=min.intValue(); i<=max.intValue(); i++){
            BigDecimal value = BigDecimal.valueOf(i).divide(BigDecimal.valueOf(100)).setScale(2);
            try {
                BlockingQueue<ShareOrder> buyShareOrders = Database.shareOrder.get(ShareCode.ALPHA).get(value).get(ShareOrderStatus.BUY);
                BlockingQueue<ShareOrder> sellShareOrders = Database.shareOrder.get(ShareCode.ALPHA).get(value).get(ShareOrderStatus.SELL);
            }catch (NullPointerException e){
                Database.shareOrder.get(ShareCode.ALPHA).put(value, new HashMap<>());
                Database.shareOrder.get(ShareCode.ALPHA).get(value).put(ShareOrderStatus.BUY, new LinkedBlockingQueue<>());
                Database.shareOrder.get(ShareCode.ALPHA).get(value).put(ShareOrderStatus.SELL, new LinkedBlockingQueue<>());
            }
        }
        return this;
    }

}
