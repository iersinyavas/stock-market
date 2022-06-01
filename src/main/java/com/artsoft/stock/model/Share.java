package com.artsoft.stock.model;

import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.SystemConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class Share {

    private ShareCode shareCode;
    private BigDecimal startPrice;
    private BigDecimal currentBuyPrice;
    private BigDecimal currentSellPrice;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private BigDecimal lot;
    private Spread spread;
    Object lock = new Object();

    public Share(ShareCode shareCode){
        this.shareCode = shareCode;
        this.startPrice = RandomData.randomStartPrice().setScale(2);
        this.currentBuyPrice = this.startPrice;
        this.currentSellPrice = this.startPrice.add(BigDecimal.valueOf(0.01));
        this.maxPrice = this.startPrice.add(this.startPrice.divide(BigDecimal.TEN)).setScale(2);
        this.minPrice = this.startPrice.subtract(this.startPrice.divide(BigDecimal.TEN)).setScale(2);
        this.spread = new Spread(this);
        log.info("Hisse Max: {} Min: {}", this.maxPrice, this.minPrice);
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public void setCurrentBuyPrice(BigDecimal currentBuyPrice) {
        synchronized (lock){
            this.currentBuyPrice = currentBuyPrice;
        }
    }

    public void setCurrentBuyPrice() {
        this.currentBuyPrice = this.startPrice;
    }

    public void setCurrentSellPrice() {
        this.currentSellPrice = this.startPrice.add(BigDecimal.valueOf(0.01));;
    }

    public void setMaxPrice() {
        this.maxPrice = this.startPrice.add(this.startPrice.divide(BigDecimal.TEN)).setScale(2, RoundingMode.FLOOR);
    }

    public void setMinPrice() {
        this.minPrice = this.startPrice.subtract(this.startPrice.divide(BigDecimal.TEN)).setScale(2, RoundingMode.FLOOR);
    }

    public void updateShare(){
        synchronized (lock){
            this.setStartPrice(this.getCurrentBuyPrice());
            this.setCurrentBuyPrice();
            this.setCurrentSellPrice();
            this.setMaxPrice();
            this.setMinPrice();
            this.setSpread(this.getSpread().createSpread(this));
            log.info("Hisse Max: {} Min: {}", this.getMaxPrice(), this.getMinPrice());
        }
    }

    public void dividendPayment(){

    }

    public void freeCapitalIncrease(BigDecimal freeCapitalIncreaseRate){
        Set<String> customerNames = Database.customerMap.keySet();
        BigDecimal rate = Objects.isNull(freeCapitalIncreaseRate) ? SystemConstants.FREE_CAPITAL_INCREASE_RATE : freeCapitalIncreaseRate;
        rate = rate.divide(BigDecimal.valueOf(100), 2, RoundingMode.FLOOR).setScale(2, RoundingMode.FLOOR).add(BigDecimal.valueOf(1));
        for (String customerName : customerNames){
            HaveShareInformation haveShareInformation = Database.customerMap.get(customerName).getPortfolio().getHaveShareInformationMap().get(this.getShareCode());
            haveShareInformation.setHaveShareLot(haveShareInformation.getHaveShareLot().multiply(rate));
        }
        this.setStartPrice(this.getCurrentBuyPrice().divide(rate, 2, RoundingMode.FLOOR));
        this.updateShare();
    }

}
