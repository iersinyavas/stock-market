package com.artsoft.stock.util;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Setter
@Getter
@Slf4j
public class PriceStep {

    private BigDecimal price;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BlockingQueue<ShareOrder> limitBuyShareOrderQueue = new LinkedBlockingQueue();
    private BlockingQueue<ShareOrder> limitSellShareOrderQueue = new LinkedBlockingQueue();
    public static BlockingQueue<ShareOrder> marketShareOrderQueue = new LinkedBlockingQueue();

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
        log.info("Fiyat: {} Adres: {} Sonraki nesne: {}", this.getPrice(), this, this.getPriceStepUp());
        return this;
    }

    public PriceStep initDownPrice(Map<BigDecimal, BlockingQueue> priceMap){
        if (this.getPrice().compareTo(this.getMinPrice()) > 0){
            this.setPriceStepDown(new PriceStep(this.getPrice().subtract(BigDecimal.valueOf(0.01)), this.getMinPrice(), this.getMaxPrice()).initDownPrice(priceMap));
            this.getPriceStepDown().setPriceStepUp(this);
        }
        log.info("Fiyat: {} Adres: {} Önceki nesne: {}", this.getPrice(), this, this.getPriceStepDown());
        return this;
    }

    public PriceStep priceUp(PriceStep priceStep){
        if (priceStep.getPrice().compareTo(priceStep.getMaxPrice()) == 0){
            return priceStep;
        }
        if (priceStep.getPriceStepUp().getLimitSellShareOrderQueue().isEmpty()){
            return priceStep.priceUp(priceStep.getPriceStepUp());
        }
        return priceStep.getPriceStepUp();
    }

    public PriceStep priceDown(PriceStep priceStep){
        if (priceStep.getPrice().compareTo(priceStep.getMinPrice()) == 0){
            return priceStep;
        }
        if (priceStep.getPriceStepDown().getLimitBuyShareOrderQueue().isEmpty()){
            return priceStep.priceDown(priceStep.getPriceStepDown());
        }
        return priceStep.getPriceStepDown();
    }

    public BlockingQueue<ShareOrder> getPrice(ShareOrder shareOrder) {
        if (shareOrder.getPrice().compareTo(this.getPrice()) == 0){
            if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY.name())){
                return this.getLimitBuyShareOrderQueue();
            }else {
                return this.getLimitSellShareOrderQueue();
            }
        }else if(shareOrder.getPrice().compareTo(this.getPrice()) < 0){
            return this.getPriceStepDown().getPrice(shareOrder);
        }else {
            return this.getPriceStepUp().getPrice(shareOrder);
        }
    }

    public PriceStep getMaxPrice(PriceStep priceStep){
        if (Objects.nonNull(priceStep.getPriceStepUp())){
            return priceStep.getMaxPrice(priceStep.getPriceStepUp());
        }
        return priceStep;
    }

    public PriceStep getMinPrice(PriceStep priceStep){
        if (Objects.nonNull(priceStep.getPriceStepDown())){
            return priceStep.getMinPrice(priceStep.getPriceStepDown());
        }
        return priceStep;
    }
}


