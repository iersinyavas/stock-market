package com.artsoft.stock.service.broker;

import com.artsoft.stock.request.StockMarketRequest;
import com.artsoft.stock.service.operation.LimitOperation;
import com.artsoft.stock.service.operation.MarketOperation;
import com.artsoft.stock.service.operation.OperationService;
import com.artsoft.stock.service.share.ShareService;
import com.artsoft.stock.util.BatchJobLauncher;
import com.artsoft.stock.util.BatchUtil;
import com.artsoft.stock.util.Factory;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.constant.GeneralEnumeration.*;
import com.artsoft.stock.util.PriceStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMarketService {

    private final OperationService operationService;
    private final Factory factory;
    private final BatchJobLauncher batchJobLauncher;
    private final BatchUtil batchUtil;
    private final ShareService shareService;

    public void sendShareOrderToStockMarket(Share share, ShareOrder shareOrder) throws InterruptedException {
        if (shareOrder.getShareOrderType().equals(ShareOrderType.LIMIT.name())){
            share.getPriceStep().getPrice(shareOrder).put(shareOrder);
        }else {
            PriceStep.marketShareOrderQueue.put(shareOrder);
        }
        log.info("Gönderilen Emir : {}", shareOrder);
    }

    private BlockingQueue<ShareOrder> shareOrderQueueIsEmpty(BlockingQueue<ShareOrder> shareOrderQueue){
        if (!shareOrderQueue.isEmpty()){
            return shareOrderQueue;
        }
        return null;
    }

    public void matchShareOrder(Share share) throws InterruptedException, InsufficientBalanceException {

        BlockingQueue<ShareOrder> limitSellShareOrderQueue = share.getPriceStep().getLimitSellShareOrderQueue();
        BlockingQueue<ShareOrder> limitBuyShareOrderQueue = share.getPriceStep().getLimitBuyShareOrderQueue();

        BlockingQueue<ShareOrder> shareOrderQueue = this.shareOrderQueueIsEmpty(PriceStep.marketShareOrderQueue);
        if (Objects.nonNull(shareOrderQueue)){
            ShareOrder shareOrder = shareOrderQueue.peek();
            MarketOperation marketOperation = factory.getOperationService(shareOrder.getShareOrderStatus());
            if (marketOperation.execute(share, shareOrderQueue, shareOrder)) return;
        }

        LimitOperation limitOperation = factory.getLimitOperation();
        limitOperation.execute(limitSellShareOrderQueue, limitBuyShareOrderQueue);

        operationService.setPrice(share, limitSellShareOrderQueue, limitBuyShareOrderQueue);
    }


    public void launchStockMarketStart() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        StockMarketRequest stockMarketRequest = new StockMarketRequest();
        stockMarketRequest.setBatchName("stockMarketStartJob");
        stockMarketRequest.setCode("ALPHA");
        batchUtil.setShare(shareService.getShare(stockMarketRequest.getCode()));
        batchJobLauncher.launch(stockMarketRequest);
    }

    public void launchStockMarketStop() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        StockMarketRequest stockMarketRequest = new StockMarketRequest();
        stockMarketRequest.setBatchName("stockMarketCloseJob");
        stockMarketRequest.setCode("ALPHA");
        batchJobLauncher.launch(stockMarketRequest);
    }





}
