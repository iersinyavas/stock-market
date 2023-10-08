package com.artsoft.stock.util;

import com.artsoft.stock.StockApplication;
import com.artsoft.stock.request.StockMarketRequest;
import com.artsoft.stock.service.share.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchJobLauncher {

    private final JobLauncher jobLauncher;
    private final BatchUtil batchUtil;
    private final ShareService shareService;

    public void launch(StockMarketRequest stockMarketRequest) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("start", System.currentTimeMillis())
                .addString("code", stockMarketRequest.getCode())
                .toJobParameters();
        Job job = (Job) StockApplication.applicationContext.getBean(stockMarketRequest.getBatchName());
        jobLauncher.run(job, jobParameters);
    }

    public JobParameters getJobParameters(ChunkContext chunkContext){
        //Long shareId = chunkContext.getStepContext().getStepExecution().getJobParameters().getLong("code");
        return chunkContext.getStepContext().getStepExecution().getJobParameters();
    }
}
