package com.artsoft.stock.dto;

import com.artsoft.stock.util.PriceStep;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareDTO {

    private String shareCode;
    private PriceStep priceStep;

}
