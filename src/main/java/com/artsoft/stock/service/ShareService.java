package com.artsoft.stock.service;

import com.artsoft.stock.dto.ShareDTO;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.util.BatchUtil;
import com.artsoft.stock.util.PriceStep;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareRepository shareRepository;
    private final BatchUtil batchUtil;

    public void save(Share share){
        shareRepository.save(share);
    }

    public Share getShare(String code){
        return shareRepository.findByCode(code);
    }

    public Share init(Share share){
        BigDecimal price = share.getPrice();
        Map<BigDecimal, BlockingQueue> priceMap = new HashMap<>();
        BigDecimal maxPrice = price.add(price.divide(BigDecimal.valueOf(10), 2, RoundingMode.FLOOR));
        BigDecimal minPrice = price.subtract(price.divide(BigDecimal.valueOf(10), 2, RoundingMode.FLOOR));
        PriceStep priceStep = new PriceStep(price, minPrice, maxPrice);
        priceStep.initUpPrice(priceMap);
        priceStep.initDownPrice(priceMap);
        share.setPriceStep(priceStep);
        return share;
    }
}
