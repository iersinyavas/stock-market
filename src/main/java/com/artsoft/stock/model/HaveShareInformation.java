package com.artsoft.stock.model;

import com.artsoft.stock.model.share.ShareCertificate;
import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.util.SystemConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HaveShareInformation {
    private BlockingQueue<ShareCertificate> haveShareLot = new LinkedBlockingQueue<>();
    private BlockingQueue<ShareCertificate> availableHaveShareLot = new LinkedBlockingQueue<>();
    private BigDecimal averagePrice;
    private BigDecimal totalCost;
    private BigDecimal currentTotalValue;

    public HaveShareInformation(BigDecimal averagePrice){
        try {
            for (int i = 0; i< SystemConstants.START_HAVE_SHARE_LOT.intValue(); i++){
                this.haveShareLot.put(new ShareCertificate(ShareCode.ALPHA));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.availableHaveShareLot = haveShareLot;
        this.averagePrice = averagePrice;
        this.totalCost = this.averagePrice.multiply(BigDecimal.valueOf(haveShareLot.size()));
    }

    public synchronized BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public synchronized void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public synchronized BigDecimal getTotalCost() {
        return totalCost;
    }

    public synchronized void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public synchronized BigDecimal getCurrentTotalValue() {
        return currentTotalValue;
    }

    public synchronized void setCurrentTotalValue(BigDecimal currentTotalValue) {
        this.currentTotalValue = currentTotalValue;
    }
}
