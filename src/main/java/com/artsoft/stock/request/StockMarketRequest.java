package com.artsoft.stock.request;

import lombok.Data;

@Data
public class StockMarketRequest {

    private String batchName;
    private String code;
}
