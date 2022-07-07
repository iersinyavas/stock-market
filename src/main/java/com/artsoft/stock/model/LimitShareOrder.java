package com.artsoft.stock.model;

import com.artsoft.stock.exception.WrongLotInformationException;
import com.artsoft.stock.model.share.ShareCertificate;
import com.artsoft.stock.util.GeneralEnumeration;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.SystemConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.LinkedBlockingQueue;

public class LimitShareOrder extends ShareOrder {

    public LimitShareOrder(Share share, BigDecimal balance, HaveShareInformation haveShareInformation) throws WrongLotInformationException {
        super(share, balance, haveShareInformation);
        int buyLot = 0;
        int haveShareLot = haveShareInformation.getHaveShareLot().size();
        int sellLot = (haveShareLot == 0) ? 0 : RandomData.randomLot(haveShareLot).intValue();
        if (this.shareOrderStatus.equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
            this.price = RandomData.randomShareOrderPrice(share.getMinPrice(), share.getMaxPrice());
            if (price.compareTo(share.getCurrentBuyPrice()) > 0){
                price = share.getCurrentSellPrice();
            }
            buyLot = balance.divide(price, 2, RoundingMode.FLOOR).intValue();
            buyLot = (buyLot == 0) ? 0 : RandomData.randomLot(buyLot);
            this.tempLot = (buyLot <= SystemConstants.SHARE_LOT.intValue() && buyLot >= 0) ? BigDecimal.valueOf(buyLot) : BigDecimal.valueOf(RandomData.randomLot(SystemConstants.SHARE_LOT.intValue()));
            if (this.tempLot.compareTo(BigDecimal.ZERO)<=0){
                throw new WrongLotInformationException();
            }
            this.lot = new LinkedBlockingQueue<ShareCertificate>(this.tempLot.intValue());
            this.cost = price.multiply(tempLot).setScale(2);
        }else {
            this.price = RandomData.randomShareOrderPrice(share.getMinPrice(), share.getMaxPrice());
            if (this.price.compareTo(share.getCurrentSellPrice()) < 0){
                this.price = share.getCurrentBuyPrice();
            }
            this.tempLot = BigDecimal.valueOf(sellLot);
            if (this.tempLot.compareTo(BigDecimal.ZERO)<=0){
                throw new WrongLotInformationException();
            }
            this.lot = new LinkedBlockingQueue<ShareCertificate>(this.tempLot.intValue());
            this.cost = price.multiply(tempLot).setScale(2);
        }
    }
}
