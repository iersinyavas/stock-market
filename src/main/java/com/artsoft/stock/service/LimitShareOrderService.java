package com.artsoft.stock.service;

import com.artsoft.stock.constant.GeneralEnumeration;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.SwapProcess;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.util.ShareOrderUtil;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;

@Service
public class LimitShareOrderService extends OperationService implements LimitOperation{

    public LimitShareOrderService(TraderRepository traderRepository, ShareOrderRepository shareOrderRepository, ShareOrderUtil shareOrderUtil, CandleStickService candleStickService) {
        super(traderRepository, shareOrderRepository, shareOrderUtil, candleStickService);
    }

    @Override
    public void execute(BlockingQueue<ShareOrder> limitSellShareOrderQueue, BlockingQueue<ShareOrder> limitBuyShareOrderQueue) throws InterruptedException {
        while(!limitSellShareOrderQueue.isEmpty() && !limitBuyShareOrderQueue.isEmpty()){
            ShareOrder sell = limitSellShareOrderQueue.peek();
            ShareOrder buy = limitBuyShareOrderQueue.peek();
            SwapProcess swapProcess = new SwapProcess();
            swapProcess.setShareOrderStatus(sell.getCreateTime().compareTo(buy.getCreateTime()) > 0 ? sell.getShareOrderStatus() : buy.getShareOrderStatus());
            swapProcess.setShareOrderType(GeneralEnumeration.ShareOrderType.LIMIT.name());
            if (sell.getLot().compareTo(buy.getLot()) < 0){
                this.ifBuyGreaterThanSell(limitSellShareOrderQueue, sell, buy, swapProcess);
            }else if(sell.getLot().compareTo(buy.getLot()) > 0){
                this.ifSellGreaterThanBuy(limitBuyShareOrderQueue, sell, buy, swapProcess);
            }else{
                this.ifSellEqualsBuy(limitSellShareOrderQueue, limitBuyShareOrderQueue, sell, buy, swapProcess);
            }
        }
    }
}
