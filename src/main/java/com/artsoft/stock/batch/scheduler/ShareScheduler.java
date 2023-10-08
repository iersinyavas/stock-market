package com.artsoft.stock.batch.scheduler;

import com.artsoft.stock.service.broker.StockMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(value = "scheduler.cron.share.enabled", matchIfMissing = true)
public class ShareScheduler {

    private final StockMarketService stockMarketService;
    @Scheduled(cron = "${scheduler.cron.share.expression}")
    public void start() throws InterruptedException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        stockMarketService.launchStockMarketStart();
        Thread.sleep(30000);
        stockMarketService.launchStockMarketStop();
    }
}
