package com.artsoft.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareSummaryInfoTransport {
    private BigDecimal currentSellPrice;
    private BigDecimal currentBuyPrice;
}
