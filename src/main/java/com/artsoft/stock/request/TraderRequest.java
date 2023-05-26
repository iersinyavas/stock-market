package com.artsoft.stock.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TraderRequest {
    private Long traderId;
    private String name;
    private BigDecimal balance;
    private BigDecimal cost;
    private BigDecimal haveLot;
    private BigDecimal currentHaveLot;
}
