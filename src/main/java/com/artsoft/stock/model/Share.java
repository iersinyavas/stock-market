package com.artsoft.stock.model;

import com.artsoft.stock.model.share.ShareCertificate;
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
import java.util.concurrent.BlockingQueue;
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
    private BlockingQueue<ShareCertificate> shareCertificateQueue = new LinkedBlockingQueue<>();
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
        /*try {
            for (int i=0; i<10000; i++){
                this.getShareCertificateQueue().put(new ShareCertificate(this.getShareCode()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        log.info("Hisse Max: {} Min: {}", this.maxPrice, this.minPrice);
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
    }

    public void freeCapitalIncrease(){
        try {
            for (int i=0; i<10000; i++){
                this.getShareCertificateQueue().put(new ShareCertificate(this.getShareCode()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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


}
