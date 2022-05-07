package com.artsoft.stock.model;

import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Share {

    private ShareCode shareCode;
    private BigDecimal startPrice;
    private BigDecimal currentBuyPrice;
    private BigDecimal currentSellPrice;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private Spread spread;

    public Share(ShareCode shareCode){
        this.shareCode = shareCode;
        this.startPrice = RandomData.randomStartPrice().setScale(2);
        this.currentBuyPrice = this.startPrice;
        this.currentSellPrice = this.startPrice.add(BigDecimal.valueOf(0.01));
        this.maxPrice = this.startPrice.add(this.startPrice.divide(BigDecimal.TEN)).setScale(2);
        this.minPrice = this.startPrice.subtract(this.startPrice.divide(BigDecimal.TEN)).setScale(2);
        this.spread = new Spread(this);
    }

    public void updateShareMaxMinPrice(Share share){
        share.setMaxPrice(share.startPrice.add(share.startPrice.divide(BigDecimal.TEN)).setScale(2));
        share.setMinPrice(share.startPrice.subtract(share.startPrice.divide(BigDecimal.TEN)).setScale(2));
    }
}
