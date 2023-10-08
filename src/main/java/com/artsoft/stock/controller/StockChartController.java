package com.artsoft.stock.controller;

import com.artsoft.stock.dto.StockChart;
import com.artsoft.stock.response.BaseResponse;
import com.artsoft.stock.service.StockChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/stock-chart")
@RequiredArgsConstructor
@CrossOrigin
public class StockChartController {

    private final StockChartService stockChartService;

    @GetMapping(value = "/stock-chart-info", produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<BaseResponse<StockChart>> stockChartInfo() {
        StockChart stockChart = stockChartService.stockChartInfo();
        return ResponseEntity.ok(new BaseResponse<>(stockChart));
    }

}
