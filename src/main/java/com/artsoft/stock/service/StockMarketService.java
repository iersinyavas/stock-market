package com.artsoft.stock.service;

import com.artsoft.stock.util.Factory;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.constant.GeneralEnumeration.*;
import com.artsoft.stock.util.PriceStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMarketService {

    private final OperationService operationService;
    private final Factory factory;

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
        //BlockingQueue<ShareOrder> shareOrderQueue = this.shareOrderQueueIsEmpty(PriceStep.marketSellShareOrderQueue);

        //TODO Market emirlerine buy ve sell diye ayırarak işlem yapılacak gerekli düzenlemeyi yap
        // Zaten buy için max fiyatta sell kuyruğu devreye girecek
        // sell için min fiyatta buy kuyruğu devreye girer yarım kalan market orderlarda

        MarketOperation marketOperation = factory.getOperationService(ShareOrderStatus.BUY.name());
        if (Objects.nonNull(shareOrderQueue)){
            ShareOrder shareOrder = shareOrderQueue.peek();
            if (marketOperation.execute(share, shareOrderQueue, shareOrder)) return;
        }

        LimitOperation limitOperation = factory.getLimitOperation();
        limitOperation.execute(limitSellShareOrderQueue, limitBuyShareOrderQueue);

        operationService.setPrice(share, limitSellShareOrderQueue, limitBuyShareOrderQueue);
    }






}
