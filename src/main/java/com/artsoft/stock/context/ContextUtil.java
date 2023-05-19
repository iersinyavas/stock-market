package com.artsoft.stock.context;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.scope.context.ChunkContext;

import java.io.Serializable;


public class ContextUtil implements Serializable {

    private ContextUtil() {
    }

    private static final String CONTEXT = "CONTEXT";

    public static void putContext(JobExecution jobExecution, StockMarketBatchContext context) {
        jobExecution.getExecutionContext().put(CONTEXT, context);
    }

    public static StockMarketBatchContext getJobExecutionContext(ChunkContext chunkContext) {
        return (StockMarketBatchContext) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get(CONTEXT);
    }

    public static JobExecution getJobExecution(ChunkContext chunkContext) {
        return chunkContext.getStepContext().getStepExecution().getJobExecution();
    }
}
