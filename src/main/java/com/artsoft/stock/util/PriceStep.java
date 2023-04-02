package com.artsoft.stock.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Setter
@Getter
@Slf4j
public class PriceStep {

    private BigDecimal price;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BlockingQueue shareOrderQueue = new LinkedBlockingQueue();

    private PriceStep priceStepUp;
    private PriceStep priceStepDown;

    public PriceStep(BigDecimal price, BigDecimal minPrice, BigDecimal maxPrice) {
        this.price = price;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public PriceStep initUpPrice(Map<BigDecimal, BlockingQueue> priceMap){
        if (this.getPrice().compareTo(this.getMaxPrice()) < 0){
            this.setPriceStepUp(new PriceStep(this.getPrice().add(BigDecimal.valueOf(0.01)), this.getMinPrice(), this.getMaxPrice()).initUpPrice(priceMap));
            this.getPriceStepUp().setPriceStepDown(this);
        }
        priceMap.put(this.getPrice(), this.getShareOrderQueue());
        log.info("Fiyat: {} Adres: {} Sonraki nesne: {}", this.getPrice(), this, this.getPriceStepUp());
        return this;
    }

    public PriceStep initDownPrice(Map<BigDecimal, BlockingQueue> priceMap){
        if (this.getPrice().compareTo(this.getMinPrice()) > 0){
            this.setPriceStepDown(new PriceStep(this.getPrice().subtract(BigDecimal.valueOf(0.01)), this.getMinPrice(), this.getMaxPrice()).initDownPrice(priceMap));
            this.getPriceStepDown().setPriceStepUp(this);
        }
        priceMap.put(this.getPrice(), this.getShareOrderQueue());
        log.info("Fiyat: {} Adres: {} Önceki nesne: {}", this.getPrice(), this, this.getPriceStepDown());
        return this;
    }

}


