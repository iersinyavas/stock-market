package com.artsoft.stock.util;

import com.artsoft.stock.StockApplication;
import com.artsoft.stock.constant.BeanName;
import com.artsoft.stock.service.*;
import com.artsoft.stock.constant.GeneralEnumeration.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Factory {

    private final BuyService buyService;
    private final SellService sellService;

    private Map<String, MarketOperation> operationServiceMap = new HashMap<>();
    @PostConstruct
    private void init(){
        operationServiceMap.put(ShareOrderStatus.BUY.name(), buyService);
        operationServiceMap.put(ShareOrderStatus.SELL.name(), sellService);
    }

    public MarketOperation getOperationService(String shareOrderStatus) {
        return operationServiceMap.get(shareOrderStatus);
    }

    public LimitOperation getLimitOperation(){
        return (LimitOperation) StockApplication.applicationContext.getBean(BeanName.LIMIT_SHARE_ORDER_SERVICE);
    }

    public Payable getPayable(String fundIncrease){
        return (Payable) StockApplication.applicationContext.getBean(fundIncrease);
    }
}
