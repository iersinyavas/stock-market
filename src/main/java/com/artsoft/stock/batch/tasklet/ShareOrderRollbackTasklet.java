package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.service.ShareOrderService;
import com.artsoft.stock.service.TraderService;
import com.artsoft.stock.util.BatchUtil;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.PriceStep;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

@Component
@RequiredArgsConstructor
public class ShareOrderRollbackTasklet implements Tasklet {

    private final ShareOrderService shareOrderService;
    private final BatchUtil batchUtil;
    private final TraderService traderService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws InterruptedException {
        try {
            Thread.sleep(5000);
            Share share = batchUtil.getShare();
            share.setPrice(share.getPriceStep().getPrice());
            share.setPriceStep(share.getPriceStep().getMaxPrice(share.getPriceStep()));
            Trader trader;
            while (!PriceStep.marketShareOrderQueue.isEmpty()){
                ShareOrder shareOrder = PriceStep.marketShareOrderQueue.take();
                if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.SELL.name())){
                    trader = traderService.getTrader(shareOrder.getTrader().getTraderId());
                    trader.setHaveLot(trader.getHaveLot().add(shareOrder.getLot()));
                    traderService.save(trader);
                    shareOrderService.delete(shareOrder);
                }else {
                    shareOrderService.delete(shareOrder);
                }
            }

            while (true){
                while (this.isEmpty(share.getPriceStep())){
                    share.setPriceStep(share.getPriceStep().getPriceStepDown());
                }
                BlockingQueue<ShareOrder> buyShareOrderQueue = share.getPriceStep().getLimitBuyShareOrderQueue();
                while (!buyShareOrderQueue.isEmpty()) {
                    ShareOrder shareOrder = buyShareOrderQueue.take();
                    trader = traderService.getTrader(shareOrder.getTrader().getTraderId());
                    trader.setBalance(trader.getBalance().add(shareOrder.getVolume()));
                    traderService.save(trader);
                    shareOrderService.delete(shareOrder);
                }

                BlockingQueue<ShareOrder> sellShareOrderQueue = share.getPriceStep().getLimitSellShareOrderQueue();
                while (!sellShareOrderQueue.isEmpty()){
                    ShareOrder shareOrder = sellShareOrderQueue.take();
                    trader = traderService.getTrader(shareOrder.getTrader().getTraderId());
                    trader.setHaveLot(trader.getHaveLot().add(shareOrder.getLot()));
                    traderService.save(trader);
                    shareOrderService.delete(shareOrder);
                }

                if (Objects.isNull(share.getPriceStep().getPriceStepDown())){
                    break;
                }
            }
        }catch (InterruptedException | NullPointerException e){
            return RepeatStatus.FINISHED;
        }

        return RepeatStatus.FINISHED;
    }

    private boolean isEmpty(PriceStep priceStep){
        BlockingQueue<ShareOrder> limitSellShareOrderQueue = priceStep.getLimitSellShareOrderQueue();
        BlockingQueue<ShareOrder> limitBuyShareOrderQueue = priceStep.getLimitBuyShareOrderQueue();
        if (limitSellShareOrderQueue.isEmpty() && limitBuyShareOrderQueue.isEmpty()){
            return true;
        }
        return false;
    }


}
