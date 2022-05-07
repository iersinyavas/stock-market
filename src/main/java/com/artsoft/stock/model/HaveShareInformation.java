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
    private BigDecimal averageCost;
    private BigDecimal totalCost;
    private InvestmentResult investmentResult;

    public HaveShareInformation(BigDecimal haveShareLot, BigDecimal averageCost){
        this.haveShareLot = haveShareLot;
        this.availableHaveShareLot = haveShareLot;
        this.averageCost = averageCost;
        this.totalCost = availableHaveShareLot.multiply(averageCost);
    }
}
