package com.artsoft.stock.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SwapProcessDTO {
    private Long swapProcessId;
    private String buyer;
    private String seller;
    private BigDecimal lot;
    private String shareOrderStatus;
    private String shareOrderType;
    private BigDecimal volume;
    private BigDecimal price;
    private LocalDateTime transactionTime;
}
