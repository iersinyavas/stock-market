package com.artsoft.stock.model;

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

    public synchronized BigDecimal getAvailableHaveShareLot() {
        return availableHaveShareLot;
    }

    public synchronized void setAvailableHaveShareLot(BigDecimal availableHaveShareLot) {
        this.availableHaveShareLot = availableHaveShareLot;
    }

    public synchronized BigDecimal getHaveShareLot() {
        return haveShareLot;
    }

    public synchronized void setHaveShareLot(BigDecimal haveShareLot) {
        this.haveShareLot = haveShareLot;
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
