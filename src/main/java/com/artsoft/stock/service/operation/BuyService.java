package com.artsoft.stock.service.operation;

import com.artsoft.stock.constant.GeneralEnumeration;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.SwapProcess;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.service.CandleStickService;
import com.artsoft.stock.util.ShareOrderUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

            /*boolean continueFlag = this.balanceControl(shareOrderQueue, shareOrder, sell);
            if (continueFlag) {
                continue;
            }*/

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

    /*private boolean balanceControl(BlockingQueue<ShareOrder> shareOrderQueue, ShareOrder buy, ShareOrder sell) throws InsufficientBalanceException, InterruptedException {//Kuyruktan geldiği için buy ===> shareOrder oldu
        Trader trader = traderRepository.findById(buy.getTrader().getTraderId()).get();
        buy.setVolume(buy.getPrice().multiply(sell.getLot()));
        BigDecimal divide = BigDecimal.valueOf(trader.getBalance().divide(sell.getPrice(), RoundingMode.FLOOR).longValue());
        divide = divide.compareTo(buy.getLot()) > 0 ? buy.getLot() : divide;

        if (divide.compareTo(BigDecimal.ZERO) == 0){
            this.deleteShareOrder(shareOrderQueue, buy);
            return true;
        }

        if (divide.compareTo(sell.getLot()) <= 0){
            buy.setLot(divide);
            buy.setVolume(buy.getPrice().multiply(buy.getLot()));
        }
        return false;
    }*/

    private void balanceControl(ShareOrder buy, ShareOrder sell) throws InsufficientBalanceException {//Kuyruktan geldiği için buy ===> shareOrder oldu
        Trader trader = traderRepository.findById(buy.getTrader().getTraderId()).get();
        if (trader.getBalance().compareTo(BigDecimal.ZERO) == 0){
            throw new InsufficientBalanceException();
        }
        buy.setVolume(buy.getPrice().multiply(sell.getLot()));
        BigDecimal divide = BigDecimal.valueOf(trader.getBalance().divide(sell.getPrice(), RoundingMode.FLOOR).longValue());
        divide = divide.compareTo(buy.getLot()) > 0 ? buy.getLot() : divide;
        if (divide.compareTo(sell.getLot()) <= 0){
            buy.setLot(divide);
            buy.setVolume(buy.getPrice().multiply(buy.getLot()));
        }
    }
}
