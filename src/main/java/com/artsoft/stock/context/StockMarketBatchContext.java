package com.artsoft.stock.context;

import com.artsoft.stock.entity.Share;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class StockMarketBatchContext implements Serializable {
    private Share share;
}
