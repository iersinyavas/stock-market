package com.artsoft.stock.service.operation;

import com.artsoft.stock.constant.GeneralEnumeration;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.SwapProcess;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.service.CandleStickService;
import com.artsoft.stock.util.ShareOrderUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

@Service
public class SellService extends OperationService implements MarketOperation {

    public SellService(TraderRepository traderRepository, ShareOrderRepository shareOrderRepository, ShareOrderUtil shareOrderUtil, CandleStickService candleStickService) {
        super(traderRepository, shareOrderRepository, shareOrderUtil, candleStickService);
    }

    @Override
    public Boolean execute(Share share, BlockingQueue<ShareOrder> shareOrderQueue, ShareOrder shareOrder) throws InterruptedException {
        BlockingQueue<ShareOrder> limitBuyShareOrderQueue;
        while (shareOrder.getLot().compareTo(BigDecimal.ZERO) > 0){
            if (share.getPriceStep().getLimitBuyShareOrderQueue().isEmpty()){
                share.setPriceStep(share.getPriceStep().priceDown(share.getPriceStep()));
            }
            candleStickService.saveAndSendSwapProcess(null, null, share);
            shareOrder.setPrice(share.getPriceStep().getPrice());
            limitBuyShareOrderQueue = share.getPriceStep().getLimitBuyShareOrderQueue();
            ShareOrder buy = limitBuyShareOrderQueue.peek();
            if (Objects.isNull(buy)){
                Trader trader = traderRepository.findById(shareOrder.getTrader().getTraderId()).get();
                trader.setHaveLot(trader.getHaveLot().add(shareOrder.getLot()));
                traderRepository.save(trader);
                //this.deleteShareOrder(shareOrderQueue, shareOrder);
                return true;
            }
            SwapProcess swapProcess = new SwapProcess();
            swapProcess.setShareOrderStatus(GeneralEnumeration.ShareOrderStatus.SELL.name());
            swapProcess.setShareOrderType(shareOrder.getShareOrderType());
            if (shareOrder.getLot().compareTo(buy.getLot()) < 0){
                shareOrder.setVolume(shareOrder.getPrice().multiply(shareOrder.getLot()));
                this.ifBuyGreaterThanSell(shareOrderQueue, shareOrder, buy, swapProcess);
            }else if(shareOrder.getLot().compareTo(buy.getLot()) > 0){
                shareOrder.setVolume(shareOrder.getPrice().multiply(buy.getLot()));
                this.ifSellGreaterThanBuy(limitBuyShareOrderQueue, shareOrder, buy, swapProcess);
            }else{
                shareOrder.setVolume(shareOrder.getPrice().multiply(shareOrder.getLot()));
                this.ifSellEqualsBuy(shareOrderQueue, limitBuyShareOrderQueue, shareOrder, buy, swapProcess);
            }
        }

        return true;
    }
}
