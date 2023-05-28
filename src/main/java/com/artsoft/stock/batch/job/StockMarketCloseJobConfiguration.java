package com.artsoft.stock.batch.job;

import com.artsoft.stock.base.BaseBatchConfiguration;
import com.artsoft.stock.batch.step.*;
import com.artsoft.stock.context.StockMarketBatchContext;
import com.artsoft.stock.listener.StockMarketJobListener;
import com.artsoft.stock.util.StockMarketConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StockMarketCloseJobConfiguration extends BaseBatchConfiguration {
    private final ShareOrderThreadStopStep shareOrderThreadStopStep;
    private final UpdateSharePriceStep updateSharePriceStep;
    private final ShareOrderRollbackStep shareOrderRollbackStep;
    private final DeleteTraderStep deleteTraderStep;
    private final AddMoneyBalanceStep addMoneyBalanceStep;

    @Override
    @Bean(name= StockMarketConstant.STOCK_MARKET_CLOSE)
    protected Job createJob() {
        List<Step> stepList = new ArrayList<>();
        this.initContext();
        stepList.add(shareOrderThreadStopStep.jobStep());
        stepList.add(shareOrderRollbackStep.jobStep());
        stepList.add(updateSharePriceStep.jobStep());
        //stepList.add(deleteTraderStep.jobStep());
        stepList.add(addMoneyBalanceStep.jobStep());
        //Maaş eklemesi stepinide ekle
        SimpleJobBuilder simpleJobBuilder = super.createSimpleJobBuilder(StockMarketConstant.STOCK_MARKET_CLOSE, stepList);
        return simpleJobBuilder.listener(new StockMarketJobListener(this.initContext())).build();
    }

    @Override
    protected StockMarketBatchContext initContext() {
        return StockMarketBatchContext.builder()
                .share(batchUtil.getShare())
                .build();
    }

    @Override
    protected JobExecutionListener createExceptionListener() {
        return new StockMarketJobListener(this.initContext());
    }




}
