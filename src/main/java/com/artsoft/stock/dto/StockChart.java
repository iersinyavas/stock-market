package com.artsoft.stock.dto;

import lombok.Data;

import java.util.List;

@Data
public class StockChart {
    private List<CandleStick> candleStickList;
}
