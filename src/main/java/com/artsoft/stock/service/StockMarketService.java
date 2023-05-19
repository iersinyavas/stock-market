package com.artsoft.stock.service;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.SwapProcess;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.SwapProcessRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.util.BatchUtil;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.PriceStep;
import com.artsoft.stock.util.ShareOrderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMarketService {

    private final TraderRepository traderRepository;
    private final ShareOrderRepository shareOrderRepository;
    private final ShareOrderUtil shareOrderUtil;
    private final SwapProcessRepository swapProcessRepository;
    

    public void sendShareOrderToStockMarket(Share share, ShareOrder shareOrder) throws InterruptedException {
        if (shareOrder.getShareOrderType().equals(ShareOrderType.LIMIT.name())){
            share.getPriceStep().getPrice(shareOrder).put(shareOrder);
        }else {
            if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.BUY)){
                PriceStep.marketBuyShareOrderQueue.put(shareOrder);
            }else {
                PriceStep.marketSellShareOrderQueue.put(shareOrder);
            }
        }
    }

    public void matchShareOrder(Share share) throws InterruptedException {

        BlockingQueue<ShareOrder> limitSellShareOrderQueue = share.getPriceStep().getLimitSellShareOrderQueue();
        BlockingQueue<ShareOrder> limitBuyShareOrderQueue = share.getPriceStep().getLimitBuyShareOrderQueue();

        while(!limitSellShareOrderQueue.isEmpty() && !limitBuyShareOrderQueue.isEmpty()){
            ShareOrder sell = limitSellShareOrderQueue.peek();
            ShareOrder buy = limitBuyShareOrderQueue.peek();
            SwapProcess swapProcess;
            if (sell.getLot().compareTo(buy.getLot()) < 0){
                swapProcess = new SwapProcess();
                swapProcess.setVolume(sell.getVolume());
                swapProcess.setPrice(sell.getPrice());
                Trader traderSell = traderRepository.findById(sell.getTrader().getTraderId()).get();
                traderSell.setCurrentHaveLot(traderSell.getCurrentHaveLot().subtract(sell.getLot()));

                Trader traderBuy = traderRepository.findById(buy.getTrader().getTraderId()).get();
                traderBuy.setCurrentHaveLot(traderBuy.getCurrentHaveLot().add(sell.getLot()));
                traderBuy.setHaveLot(traderBuy.getHaveLot().add(sell.getLot()));
                this.swapProcess(sell, buy, swapProcess, traderSell, traderBuy);

                buy.setLot(buy.getLot().subtract(sell.getLot()));
                buy.setVolume(buy.getVolume().subtract(swapProcess.getVolume()));
                shareOrderRepository.delete(sell);
                limitSellShareOrderQueue.take();
            }else if(sell.getLot().compareTo(buy.getLot()) > 0){
                swapProcess = new SwapProcess();
                swapProcess.setVolume(buy.getVolume());
                swapProcess.setPrice(buy.getPrice());
                Trader traderSell = traderRepository.findById(sell.getTrader().getTraderId()).get();
                traderSell.setCurrentHaveLot(traderSell.getCurrentHaveLot().subtract(buy.getLot()));

                Trader traderBuy = traderRepository.findById(buy.getTrader().getTraderId()).get();
                traderBuy.setCurrentHaveLot(traderBuy.getCurrentHaveLot().add(buy.getLot()));
                traderBuy.setHaveLot(traderBuy.getHaveLot().add(buy.getLot()));
                this.swapProcess(sell, buy, swapProcess, traderSell, traderBuy);

                sell.setLot(sell.getLot().subtract(buy.getLot()));
                sell.setVolume(sell.getVolume().subtract(swapProcess.getVolume()));
                shareOrderRepository.delete(buy);
                limitBuyShareOrderQueue.take();
            }else{
                swapProcess = new SwapProcess();
                swapProcess.setVolume(sell.getVolume());
                swapProcess.setPrice(sell.getPrice());
                Trader traderSell = traderRepository.findById(sell.getTrader().getTraderId()).get();
                traderSell.setCurrentHaveLot(traderSell.getCurrentHaveLot().subtract(sell.getLot()));

                Trader traderBuy = traderRepository.findById(buy.getTrader().getTraderId()).get();
                traderBuy.setCurrentHaveLot(traderBuy.getCurrentHaveLot().add(buy.getLot()));
                traderBuy.setHaveLot(traderBuy.getHaveLot().add(buy.getLot()));
                this.swapProcess(sell, buy, swapProcess, traderSell, traderBuy);

                shareOrderRepository.delete(sell);
                shareOrderRepository.delete(buy);
                limitSellShareOrderQueue.take();
                limitBuyShareOrderQueue.take();
            }
        }

        this.setPrice(share, limitSellShareOrderQueue, limitBuyShareOrderQueue);
    }

    private void setPrice(Share share, BlockingQueue<ShareOrder> limitSellShareOrderQueue, BlockingQueue<ShareOrder> limitBuyShareOrderQueue) {
        if (limitSellShareOrderQueue.isEmpty() && limitBuyShareOrderQueue.isEmpty()){

        } else if (limitSellShareOrderQueue.isEmpty()){
            share.setPriceStep(share.getPriceStep().priceUp(share.getPriceStep()));
        } else {
            share.setPriceStep(share.getPriceStep().priceDown(share.getPriceStep()));
        }
    }

    private void swapProcess(ShareOrder sell, ShareOrder buy, SwapProcess swapProcess, Trader traderSell, Trader traderBuy) {
        swapProcess.setBuyer(buy.getTrader().getName());
        swapProcess.setSeller(sell.getTrader().getName());
        swapProcess.setLot(sell.getLot());
        swapProcess.setTransactionTime(LocalDateTime.now());
        swapProcessRepository.save(swapProcess);

        this.sellProcessEnd(sell, traderSell);
        this.buyProcessEnd(buy, traderBuy);
        log.info("Gerçekleşen işlem : Alan :{} - Satan :{}", buy.getTrader().getName(), sell.getTrader().getName());
    }

    public void buyProcessEnd(ShareOrder buy, Trader traderBuy) {
        traderBuy.setCost(shareOrderUtil.costCalculate(traderBuy, buy));
        traderRepository.save(traderBuy);
    }

    public void sellProcessEnd(ShareOrder sell, Trader traderSell) {
        traderSell.setBalance(traderSell.getBalance().add(sell.getLot().multiply(sell.getPrice())));
        traderRepository.save(traderSell);
    }
}
