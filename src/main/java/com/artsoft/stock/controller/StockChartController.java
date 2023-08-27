package com.artsoft.stock.controller;

import com.artsoft.stock.dto.StockChart;
import com.artsoft.stock.entity.SwapProcess;
import com.artsoft.stock.repository.SwapProcessRepository;
import com.artsoft.stock.request.StockMarketRequest;
import com.artsoft.stock.response.BaseResponse;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.service.StockChartService;
import com.artsoft.stock.util.BatchJobLauncher;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stock-chart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockChartController {

    private final StockChartService stockChartService;

    @GetMapping(value = "/stock-chart-info", produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<BaseResponse<StockChart>> stockChartInfo() {
        StockChart stockChart = stockChartService.stockChartInfo();
        return ResponseEntity.ok(new BaseResponse<>(stockChart));
    }

}
