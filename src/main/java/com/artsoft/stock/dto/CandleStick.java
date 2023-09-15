package com.artsoft.stock.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CandleStick {
    @JsonFormat(pattern="HH:mm")
    private LocalDateTime date;
    private BigDecimal low;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal high;
    //private BigDecimal volume;

}
