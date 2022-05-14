package com.artsoft.stock.model;

import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.util.GeneralEnumeration.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HaveShareInformation {
    private BigDecimal availableHaveShareLot;
    private BigDecimal haveShareLot;
    private BigDecimal averagePrice;
    private BigDecimal totalCost;
    private BigDecimal currentTotalValue;
 //   private BigDecimal netCost;

    public HaveShareInformation(BigDecimal haveShareLot, BigDecimal averagePrice){
        this.haveShareLot = haveShareLot;
        this.availableHaveShareLot = haveShareLot;
        this.averagePrice = averagePrice;
        this.totalCost = availableHaveShareLot.multiply(this.averagePrice);
 //       this.netCost = this.totalCost;
    }
}
