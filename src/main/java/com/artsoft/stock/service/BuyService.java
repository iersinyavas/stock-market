package com.artsoft.stock.service;

import com.artsoft.stock.constant.GeneralEnumeration;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.SwapProcess;
import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.util.ShareOrderUtil;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

@Service
public class BuyService extends OperationService implements MarketOperation {

    public BuyService(TraderRepository traderRepository, ShareOrderRepository shareOrderRepository, ShareOrderUtil shareOrderUtil, CandleStickService candleStickService) {
        super(traderRepository, shareOrderRepository, shareOrderUtil, candleStickService);
    }

    @Override
    public Boolean execute(Share share, BlockingQueue<ShareOrder> shareOrderQueue, ShareOrder shareOrder) throws InterruptedException, InsufficientBalanceException {
        BlockingQueue<ShareOrder> limitSellShareOrderQueue;
        while (shareOrder.getLot().compareTo(BigDecimal.ZERO) > 0){
            if (share.getPriceStep().getLimitSellShareOrderQueue().isEmpty()){
                share.setPriceStep(share.getPriceStep().priceUp(share.getPriceStep()));
            }
            candleStickService.saveAndSendSwapProcess(null, null, share);
            shareOrder.setPrice(share.getPriceStep().getPrice());
            limitSellShareOrderQueue = share.getPriceStep().getLimitSellShareOrderQueue();
            ShareOrder sell = limitSellShareOrderQueue.peek();
            if (Objects.isNull(sell)){
                this.deleteShareOrder(shareOrderQueue, shareOrder);
                return true;
            }
            SwapProcess swapProcess = new SwapProcess();
            swapProcess.setShareOrderStatus(GeneralEnumeration.ShareOrderStatus.BUY.name());
            swapProcess.setShareOrderType(shareOrder.getShareOrderType());
            this.balanceControl(shareOrder, sell);
            if (sell.getLot().compareTo(shareOrder.getLot()) < 0){
                this.ifBuyGreaterThanSell(limitSellShareOrderQueue, sell, shareOrder, swapProcess);
            }else if(sell.getLot().compareTo(shareOrder.getLot()) > 0){
                this.ifSellGreaterThanBuy(shareOrderQueue, sell, shareOrder, swapProcess);
            }else{
                this.ifSellEqualsBuy(limitSellShareOrderQueue, shareOrderQueue, sell, shareOrder, swapProcess);
            }
        }
        return true;
    }
}
