package com.artsoft.stock.service;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.util.PriceStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Service
public class ShareService {

    @Autowired
    private ShareRepository shareRepository;

    public void closingSession(){
        Share share = shareRepository.findById(1L).get();
        share.setCloseSellPrice(share.getCurrentSellPrice());
        share.setCloseBuyPrice(share.getCurrentBuyPrice());
        share.setOpenSellPrice(share.getCloseSellPrice());
        share.setOpenBuyPrice(share.getCloseBuyPrice());
        share.setMaxPrice(share.getCloseBuyPrice().add(share.getCloseBuyPrice().divide(BigDecimal.TEN)));
        share.setMinPrice(share.getCloseBuyPrice().subtract(share.getCloseBuyPrice().divide(BigDecimal.TEN)));
    }

    public void openSession(){

    }

    public Map<BigDecimal, BlockingQueue> init(){
        BigDecimal price = BigDecimal.ONE;
        Map<BigDecimal, BlockingQueue> priceMap = new HashMap<>();
        BigDecimal maxPrice = price.add(price.divide(BigDecimal.valueOf(10), 2, RoundingMode.FLOOR));
        BigDecimal minPrice = price.subtract(price.divide(BigDecimal.valueOf(10), 2, RoundingMode.FLOOR));
        PriceStep priceStep = new PriceStep(price, minPrice, maxPrice);
        priceStep.initUpPrice(priceMap);
        priceStep.initDownPrice(priceMap);
        return priceMap;
    }
}
