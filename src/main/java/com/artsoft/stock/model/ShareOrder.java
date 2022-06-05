package com.artsoft.stock.model;

import com.artsoft.stock.model.share.ShareCertificate;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.SystemConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class ShareOrder {

    private Long id;
    private String customerName;
    private BlockingQueue<ShareCertificate> lot;
    private BigDecimal price;
    private BigDecimal cost;
    private ShareCode shareCode;
    private Boolean isActive = Boolean.TRUE;
    private ShareOrderStatus shareOrderStatus;
    private ShareOrderOperationStatus shareOrderOperationStatus;
    @JsonIgnore
    private BigDecimal tempLot = BigDecimal.ZERO;

    public ShareOrder(Share share, BigDecimal balance, HaveShareInformation haveShareInformation) {
        this.customerName = Thread.currentThread().getName();
        this.shareCode = share.getShareCode();
        this.shareOrderStatus = ShareOrderStatus.values()[RandomData.shareOrderStatusIndex()];
                                        /////////////////////Hisse toplamları fazladan çıkıyor kontrol et
        int buyLot = 0;
        int tempBuyLot = 0;
        int haveShareLot = haveShareInformation.getHaveShareLot().size();
        int sellLot = (haveShareLot == 0) ? 0 : RandomData.randomLot(haveShareLot).intValue();
       // sellLot = (sellLot <= SystemConstants.MAX_LOT && sellLot >= 0) ? sellLot : RandomData.randomLot(SystemConstants.MAX_LOT);

        try{
            if (shareOrderStatus.equals(ShareOrderStatus.BUY)) {
                this.price = RandomData.randomShareOrderPrice(share.getMinPrice(), share.getMaxPrice());
                if (this.price.compareTo(share.getCurrentBuyPrice()) > 0){
                    this.price = share.getCurrentSellPrice();
                }
                buyLot = balance.divide(price, 2, RoundingMode.FLOOR).intValue();
                buyLot = (buyLot == 0) ? 0 : RandomData.randomLot(buyLot);
                this.tempLot = (buyLot <= SystemConstants.SHARE_LOT.intValue() && buyLot >= 0) ? BigDecimal.valueOf(buyLot) : BigDecimal.valueOf(RandomData.randomLot(SystemConstants.SHARE_LOT.intValue()));
                this.lot = new LinkedBlockingQueue<ShareCertificate>(this.tempLot.intValue());
                this.cost = price.multiply(tempLot).setScale(2);
            }else {
                this.price = RandomData.randomShareOrderPrice(share.getMinPrice(), share.getMaxPrice());
                if (this.price.compareTo(share.getCurrentSellPrice()) < 0){
                    this.price = share.getCurrentBuyPrice();
                }
                this.tempLot = BigDecimal.valueOf(sellLot);
                this.lot = new LinkedBlockingQueue<ShareCertificate>(this.tempLot.intValue());
                this.cost = price.multiply(tempLot).setScale(2);
            }
        }catch (IllegalArgumentException e){
            this.shareOrderStatus = ShareOrderStatus.SELL;
            this.price = RandomData.randomShareOrderPrice(share.getMinPrice(), share.getMaxPrice());
            if (this.price.compareTo(share.getCurrentSellPrice()) < 0){
                this.price = share.getCurrentBuyPrice();
            }
            this.tempLot = BigDecimal.valueOf(sellLot);
            this.lot = new LinkedBlockingQueue<ShareCertificate>(this.tempLot.intValue());
            this.cost = price.multiply(tempLot).setScale(2);
        }

//        if (haveShareInformation.getAveragePrice().compareTo(share.getCurrentSellPrice()) < 0){
//            if (shareOrderStatus.equals(ShareOrderStatus.BUY)) {
//                this.price = RandomData.randomShareOrderPrice(share.getMinPrice(), share.getCurrentSellPrice());
//                buyLot = balance.divide(price, 2, RoundingMode.FLOOR).intValue();
//                buyLot = (buyLot == 0) ? 0 : RandomData.randomLot(buyLot);
//                this.lot = (buyLot <= SystemConstants.MAX_LOT && buyLot >= 0) ? BigDecimal.valueOf(buyLot) : BigDecimal.valueOf(RandomData.randomLot(SystemConstants.MAX_LOT));
//            }else {
//                this.price = RandomData.randomShareOrderPrice(share.getCurrentBuyPrice(), share.getMaxPrice());
//                this.lot = BigDecimal.valueOf(sellLot);
//            }
//        }else if(haveShareInformation.getAveragePrice().compareTo(share.getCurrentBuyPrice()) > 0){
//            if (shareOrderStatus.equals(ShareOrderStatus.BUY)) {
//                this.price = RandomData.randomShareOrderPrice(share.getMinPrice(), share.getCurrentSellPrice());
//                buyLot = balance.divide(price, 2, RoundingMode.FLOOR).intValue();
//                buyLot = (buyLot == 0) ? 0 : RandomData.randomLot(buyLot);
//                this.lot = (buyLot <= SystemConstants.MAX_LOT && buyLot >= 0) ? BigDecimal.valueOf(buyLot) : BigDecimal.valueOf(RandomData.randomLot(SystemConstants.MAX_LOT));
//            }else {
//                if (haveShareInformation.getAveragePrice().compareTo(share.getMaxPrice()) > 0){
//                    this.shareOrderStatus = ShareOrderStatus.BUY;
//                    this.price = RandomData.randomShareOrderPrice(share.getMinPrice(), share.getCurrentSellPrice());
//                    buyLot = balance.divide(price, 2, RoundingMode.FLOOR).intValue();
//                    buyLot = (buyLot == 0) ? 0 : RandomData.randomLot(buyLot);
//                    this.lot = (buyLot <= SystemConstants.MAX_LOT && buyLot >= 0) ? BigDecimal.valueOf(buyLot) : BigDecimal.valueOf(RandomData.randomLot(SystemConstants.MAX_LOT));
//                }else {
//                    this.price = RandomData.randomShareOrderPrice(haveShareInformation.getAveragePrice(), share.getMaxPrice());
//                    this.lot = BigDecimal.valueOf(sellLot);
//                }
//            }
//        }


        this.shareOrderOperationStatus = ShareOrderOperationStatus.CREATED;
    }

}
