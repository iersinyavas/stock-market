package com.artsoft.stock.batch.job;

import com.artsoft.stock.base.BaseBatchConfiguration;
import com.artsoft.stock.batch.step.*;
import com.artsoft.stock.context.StockMarketBatchContext;
import com.artsoft.stock.listener.StockMarketJobListener;
import com.artsoft.stock.constant.StockMarketConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StockMarketStartJobConfiguration extends BaseBatchConfiguration {
    private final TreaderCreateStep treaderCreateStep;
    private final ShareOrderCreateOpenSessionStep shareOrderCreateOpenSessionStep;
    private final ShareOrderCreateStep shareOrderCreateStep;
    private final ShareOrderMatcherStep shareOrderMatcherStep;
    private final FundIncreaseStep fundIncreaseStep;

    @Override
    @Bean(name= StockMarketConstant.STOCK_MARKET_START)
    protected Job createJob() {
        List<Step> stepList = new ArrayList<>();
        this.initContext();
        stepList.add(fundIncreaseStep.jobStep());
        stepList.add(treaderCreateStep.jobStep());
        stepList.add(shareOrderCreateOpenSessionStep.jobStep());
        stepList.add(shareOrderCreateStep.jobStep());
        stepList.add(shareOrderMatcherStep.jobStep());
        SimpleJobBuilder simpleJobBuilder = super.createSimpleJobBuilder(StockMarketConstant.STOCK_MARKET_START, stepList);
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
