package com.artsoft.stock.model;

import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.SystemConstants;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class ShareOrder {

    private Long id;
    private String customerName;
    private BigDecimal lot = BigDecimal.ZERO;
    private BigDecimal remainingLot;
    private BigDecimal price;
    private BigDecimal cost;
    private BigDecimal remainingCost;
    private ShareCode shareCode;
    private BigDecimal processedLot;
    private BigDecimal processedCost;
    private Boolean isActive = Boolean.TRUE;
    private ShareOrderStatus shareOrderStatus;
    private ShareOrderOperationStatus shareOrderOperationStatus;

    public ShareOrder(Share share, BigDecimal balance, HaveShareInformation haveShareInformation) {
        this.customerName = Thread.currentThread().getName();
        this.shareCode = share.getShareCode();
        this.shareOrderStatus = ShareOrderStatus.values()[RandomData.shareOrderStatusIndex()];

        int buyLot = 0;
        int tempBuyLot = 0;
        int haveShareLot = haveShareInformation.getAvailableHaveShareLot().intValue();
        int sellLot = (haveShareLot == 0) ? 0 : RandomData.randomLot(haveShareLot).intValue();
        sellLot = (sellLot <= SystemConstants.MAX_LOT && sellLot >= 0) ? sellLot : RandomData.randomLot(SystemConstants.MAX_LOT);

        if (shareOrderStatus.equals(ShareOrderStatus.BUY)) {
            this.price = RandomData.randomShareOrderPrice(share.getMinPrice(), share.getCurrentSellPrice());
            buyLot = balance.divide(price, 2, RoundingMode.FLOOR).intValue();
            buyLot = (buyLot == 0) ? 0 : RandomData.randomLot(buyLot);
            this.lot = (buyLot <= SystemConstants.MAX_LOT && buyLot >= 0) ? BigDecimal.valueOf(buyLot) : BigDecimal.valueOf(RandomData.randomLot(SystemConstants.MAX_LOT));
        }else {
            this.price = RandomData.randomShareOrderPrice(share.getCurrentBuyPrice(), share.getMaxPrice());
            this.lot = BigDecimal.valueOf(sellLot);
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

        this.remainingLot = this.lot;
        this.cost = price.multiply(lot);
        this.remainingCost = this.cost;
        this.shareOrderOperationStatus = ShareOrderOperationStatus.CREATED;
    }

}
