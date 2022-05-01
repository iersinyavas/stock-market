package com.artsoft.stock.model;

import com.artsoft.stock.model.share.ShareCode;
import com.artsoft.stock.util.RandomData;
import lombok.*;

import java.math.BigDecimal;
import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShareOrder {

    private Long id;
    private Integer lot;
    private BigDecimal price;
    private BigDecimal amount;
    private ShareCode shareCode;
    private Boolean isActive = Boolean.TRUE;

    public ShareOrder(BigDecimal balance, BigDecimal price) {
        int tempLot = balance.divide(price).intValue();
        this.lot = RandomData.randomLot(tempLot);
        this.price = price;
        this.amount = price.multiply(BigDecimal.valueOf(lot));
    }

}
