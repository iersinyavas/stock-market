package com.artsoft.stock.util;

import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.Trader;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ShareOrderUtil {

    public BigDecimal costCalculate(Trader trader, ShareOrder shareOrder){
        return trader.getCost().multiply(trader.getCurrentHaveLot())
                .add(shareOrder.getLot().multiply(shareOrder.getPrice()))
                .divide(trader.getCurrentHaveLot().add(shareOrder.getLot()), 2, RoundingMode.FLOOR);
    }
}
