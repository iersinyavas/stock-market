package com.artsoft.stock.model;

import com.artsoft.stock.model.share.ShareCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Share {

    private ShareCode shareCode;
    private BigDecimal startPrice;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    private BigDecimal max;
    private BigDecimal min;

}
