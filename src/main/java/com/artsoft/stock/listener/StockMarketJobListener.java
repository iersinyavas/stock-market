package com.artsoft.stock.listener;

import com.artsoft.stock.context.ContextUtil;
import com.artsoft.stock.context.StockMarketBatchContext;
import lombok.NonNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class StockMarketJobListener implements JobExecutionListener {

    protected final StockMarketBatchContext stockMarketBatchContext;
    //protected final FileReconciliationUtil fileReconciliationUtil;
    /*protected final ShareService shareService;
    protected final BatchUtil batchUtil;*/

    public StockMarketJobListener(StockMarketBatchContext stockMarketBatchContext){
        this.stockMarketBatchContext = stockMarketBatchContext;
        //this.fileReconciliationUtil = fileReconciliationBatchContext.getFileReconciliationUtil();
        /*this.shareService = shareService;
        this.batchUtil = batchUtil;*/
    }

    @Override
    public void beforeJob(@NonNull JobExecution jobExecution) {
        ContextUtil.putContext(jobExecution, stockMarketBatchContext);
    }

    @Override
    public void afterJob(@NonNull JobExecution jobExecution) {
        if(jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)){
            /*Share share = batchUtil.getShare();
            share.setMaxPrice(share.getPrice().add(share.getPrice().divide(BigDecimal.TEN, RoundingMode.FLOOR)));
            share.setMinPrice(share.getPrice().subtract(share.getPrice().divide(BigDecimal.TEN, RoundingMode.FLOOR)));
            shareService.save(share);*/
        }else {
            System.out.println();
        }
    }
}