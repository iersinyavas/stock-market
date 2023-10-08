package com.artsoft.stock.controller;

import com.artsoft.stock.request.StockMarketRequest;
import com.artsoft.stock.response.BaseResponse;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.service.broker.StockMarketService;
import com.artsoft.stock.util.BatchJobLauncher;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping("/stock-market")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockMarketController {

    private final BatchJobLauncher batchJobLauncher;
    private final BatchUtil batchUtil;
    private final ShareService shareService;
    private final StockMarketService stockMarketService;


    @GetMapping(value = "/start-stock-market", produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<BaseResponse<Boolean>> launchStockMarketStart() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        StockMarketRequest stockMarketRequest = new StockMarketRequest();
        stockMarketRequest.setBatchName("stockMarketStartJob");
        stockMarketRequest.setCode("ALPHA");
        batchUtil.setShare(shareService.getShare(stockMarketRequest.getCode()));
        batchJobLauncher.launch(stockMarketRequest);
        return ResponseEntity.ok(new BaseResponse<>(Boolean.TRUE));
    }

    //@Scheduled(cron = "* */3 * * * *")
    @GetMapping(value = "/stop-stock-market", produces = MediaType.APPLICATION_JSON)
    public ResponseEntity<BaseResponse<Boolean>> launchStockMarketStop() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        StockMarketRequest stockMarketRequest = new StockMarketRequest();
        stockMarketRequest.setBatchName("stockMarketCloseJob");
        stockMarketRequest.setCode("ALPHA");
        batchJobLauncher.launch(stockMarketRequest);
        return ResponseEntity.ok(new BaseResponse<>(Boolean.TRUE));
    }

}
