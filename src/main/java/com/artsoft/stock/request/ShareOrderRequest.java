package com.artsoft.stock.request;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ShareOrderRequest {

    private Long traderId;
    private BigDecimal price;
    private BigDecimal lot;
    private BigDecimal volume;
    private String shareOrderStatus;
    private String shareOrderType;

}
