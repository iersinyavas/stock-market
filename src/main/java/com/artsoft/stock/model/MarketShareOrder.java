package com.artsoft.stock.model;

import com.artsoft.stock.exception.WrongLotInformationException;
import com.artsoft.stock.model.share.ShareCertificate;
import com.artsoft.stock.util.GeneralEnumeration;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.SystemConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.LinkedBlockingQueue;

public class MarketShareOrder extends ShareOrder {

    public MarketShareOrder(Share share, BigDecimal balance, HaveShareInformation haveShareInformation) throws WrongLotInformationException {
        super(share, balance, haveShareInformation);
        int buyLot = 0;
        int haveShareLot = haveShareInformation.getHaveShareLot().size();
        int sellLot = (haveShareLot == 0) ? 0 : RandomData.randomLot(haveShareLot).intValue();

        if (this.shareOrderStatus.equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
            buyLot = balance.divide(share.getCurrentBuyPrice(), 2, RoundingMode.FLOOR).intValue();
            buyLot = (buyLot == 0) ? 0 : RandomData.randomLot(buyLot);
            this.tempLot = (buyLot <= SystemConstants.SHARE_LOT.intValue() && buyLot >= 0) ? BigDecimal.valueOf(buyLot) : BigDecimal.valueOf(RandomData.randomLot(SystemConstants.SHARE_LOT.intValue()));
            if (this.tempLot.compareTo(BigDecimal.ZERO)<=0){
                throw new WrongLotInformationException();
            }
            this.lot = new LinkedBlockingQueue<ShareCertificate>(this.tempLot.intValue());
        }else {
            this.tempLot = BigDecimal.valueOf(sellLot);
            if (this.tempLot.compareTo(BigDecimal.ZERO)<=0){
                throw new WrongLotInformationException();
            }
            this.lot = new LinkedBlockingQueue<ShareCertificate>(this.tempLot.intValue());
        }
    }
}
