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
import java.util.Objects;
import java.util.Optional;
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
            if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.BUY.name())){
                PriceStep.marketBuyShareOrderQueue.put(shareOrder);
            }else {
                PriceStep.marketSellShareOrderQueue.put(shareOrder);
            }
        }
    }

    private BlockingQueue<ShareOrder> whichShareOrderQueueNotEmpty(BlockingQueue<ShareOrder> sellShareOrderQueue, BlockingQueue<ShareOrder> buyShareOrderQueue){
        if (!sellShareOrderQueue.isEmpty()){
            return sellShareOrderQueue;
        }

        if (!buyShareOrderQueue.isEmpty()){
            return buyShareOrderQueue;
        }
        return null;
    }

    public void matchShareOrder(Share share) throws InterruptedException {

        BlockingQueue<ShareOrder> limitSellShareOrderQueue = share.getPriceStep().getLimitSellShareOrderQueue();
        BlockingQueue<ShareOrder> limitBuyShareOrderQueue = share.getPriceStep().getLimitBuyShareOrderQueue();

        BlockingQueue<ShareOrder> shareOrderQueue = this.whichShareOrderQueueNotEmpty(PriceStep.marketSellShareOrderQueue, PriceStep.marketBuyShareOrderQueue);
        if (Objects.nonNull(shareOrderQueue)){
            ShareOrder shareOrder = shareOrderQueue.peek();
            if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.BUY.name())){
                while (shareOrder.getLot().compareTo(BigDecimal.ZERO) > 0){
                    if (share.getPriceStep().getLimitSellShareOrderQueue().isEmpty()){
                        share.setPriceStep(share.getPriceStep().priceUp(share.getPriceStep()));
                    }
                    shareOrder.setPrice(share.getPriceStep().getPrice());
                    limitSellShareOrderQueue = share.getPriceStep().getLimitSellShareOrderQueue();
                    ShareOrder sell = limitSellShareOrderQueue.peek();
                    if (Objects.isNull(sell)){
                        shareOrderQueue.take();
                        shareOrderRepository.delete(shareOrder);
                        return;
                    }
                    SwapProcess swapProcess = new SwapProcess();
                    swapProcess.setShareOrderStatus(ShareOrderStatus.BUY.name());
                    if (sell.getLot().compareTo(shareOrder.getLot()) < 0){
                        shareOrder.setVolume(shareOrder.getPrice().multiply(sell.getLot()));
                        this.ifBuyGreaterThanSell(limitSellShareOrderQueue, sell, shareOrder, swapProcess);
                    }else if(sell.getLot().compareTo(shareOrder.getLot()) > 0){
                        shareOrder.setVolume(shareOrder.getPrice().multiply(shareOrder.getLot()));
                        this.ifSellGreaterThanBuy(shareOrderQueue, sell, shareOrder, swapProcess);
                    }else{
                        shareOrder.setVolume(shareOrder.getPrice().multiply(shareOrder.getLot()));
                        this.ifSellEqualsBuy(limitSellShareOrderQueue, shareOrderQueue, sell, shareOrder, swapProcess);
                    }
                }
            }else { //SELL
                while (shareOrder.getLot().compareTo(BigDecimal.ZERO) > 0){
                    if (share.getPriceStep().getLimitBuyShareOrderQueue().isEmpty()){
                        share.setPriceStep(share.getPriceStep().priceDown(share.getPriceStep()));
                    }
                    shareOrder.setPrice(share.getPriceStep().getPrice());
                    limitBuyShareOrderQueue = share.getPriceStep().getLimitBuyShareOrderQueue();
                    ShareOrder buy = limitBuyShareOrderQueue.peek();
                    if (Objects.isNull(buy)){
                        Trader trader = traderRepository.findById(shareOrder.getTrader().getTraderId()).get();                    //TODO Tabloda current_have_lot neden - oluyor bak
                        trader.setHaveLot(trader.getHaveLot().add(shareOrder.getLot()));
                        traderRepository.save(trader);
                        shareOrderQueue.take();
                        shareOrderRepository.delete(shareOrder);
                        return;
                    }
                    SwapProcess swapProcess = new SwapProcess();
                    swapProcess.setShareOrderStatus(ShareOrderStatus.SELL.name());
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
            }
            return;
        }

        while(!limitSellShareOrderQueue.isEmpty() && !limitBuyShareOrderQueue.isEmpty()){
            ShareOrder sell = limitSellShareOrderQueue.peek();
            ShareOrder buy = limitBuyShareOrderQueue.peek();
            SwapProcess swapProcess = new SwapProcess();
            if (sell.getLot().compareTo(buy.getLot()) < 0){
                this.ifBuyGreaterThanSell(limitSellShareOrderQueue, sell, buy, swapProcess);
            }else if(sell.getLot().compareTo(buy.getLot()) > 0){
                this.ifSellGreaterThanBuy(limitBuyShareOrderQueue, sell, buy, swapProcess);
            }else{
                this.ifSellEqualsBuy(limitSellShareOrderQueue, limitBuyShareOrderQueue, sell, buy, swapProcess);
            }
        }

        this.setPrice(share, limitSellShareOrderQueue, limitBuyShareOrderQueue);
    }

    private void setPrice(Share share, BlockingQueue<ShareOrder> limitSellShareOrderQueue, BlockingQueue<ShareOrder> limitBuyShareOrderQueue) {
        if (limitSellShareOrderQueue.isEmpty() && limitBuyShareOrderQueue.isEmpty()){
            share.setPriceStep(share.getPriceStep().priceDown(share.getPriceStep()));
        } else if (limitSellShareOrderQueue.isEmpty()){
            share.setPriceStep(share.getPriceStep().priceUp(share.getPriceStep()));
        } else {
            share.setPriceStep(share.getPriceStep().priceDown(share.getPriceStep()));
        }
    }

    private void ifSellEqualsBuy(BlockingQueue<ShareOrder> limitSellShareOrderQueue, BlockingQueue<ShareOrder> limitBuyShareOrderQueue, ShareOrder sell, ShareOrder buy, SwapProcess swapProcess) throws InterruptedException {
        swapProcess.setVolume(sell.getVolume()); //buy da olurdu farketmez eşitler yani
        swapProcess.setPrice(sell.getPrice());   //buy da olurdu farketmez eşitler yani
        swapProcess.setLot(sell.getLot());       //buy da olurdu farketmez eşitler yani
        Trader traderSell = traderRepository.findById(sell.getTrader().getTraderId()).get();
        traderSell.setCurrentHaveLot(traderSell.getCurrentHaveLot().subtract(sell.getLot()));
        traderRepository.save(traderSell);

        Trader traderBuy = traderRepository.findById(buy.getTrader().getTraderId()).get();
        traderBuy.setCurrentHaveLot(traderBuy.getCurrentHaveLot().add(buy.getLot()));
        traderBuy.setHaveLot(traderBuy.getHaveLot().add(buy.getLot()));
        traderRepository.save(traderBuy);
        this.swapProcess(sell, buy, swapProcess, traderSell, traderBuy);

        sell.setLot(BigDecimal.ZERO);
        buy.setLot(BigDecimal.ZERO);
        shareOrderRepository.delete(sell);
        shareOrderRepository.delete(buy);
        limitSellShareOrderQueue.take();
        limitBuyShareOrderQueue.take();
    }

    private void ifSellGreaterThanBuy(BlockingQueue<ShareOrder> limitBuyShareOrderQueue, ShareOrder sell, ShareOrder buy, SwapProcess swapProcess) throws InterruptedException {
        swapProcess.setVolume(buy.getVolume());
        swapProcess.setPrice(buy.getPrice());
        swapProcess.setLot(buy.getLot());
        Trader traderSell = traderRepository.findById(sell.getTrader().getTraderId()).get();
        traderSell.setCurrentHaveLot(traderSell.getCurrentHaveLot().subtract(buy.getLot()));
        traderRepository.save(traderSell);

        Trader traderBuy = traderRepository.findById(buy.getTrader().getTraderId()).get();
        traderBuy.setCurrentHaveLot(traderBuy.getCurrentHaveLot().add(buy.getLot()));
        traderBuy.setHaveLot(traderBuy.getHaveLot().add(buy.getLot()));
        traderRepository.save(traderBuy);
        this.swapProcess(sell, buy, swapProcess, traderSell, traderBuy);

        sell.setLot(sell.getLot().subtract(buy.getLot()));
        sell.setVolume(sell.getVolume().subtract(swapProcess.getVolume()));
        buy.setLot(BigDecimal.ZERO);
        shareOrderRepository.delete(buy);
        limitBuyShareOrderQueue.take();
    }

    private void ifBuyGreaterThanSell(BlockingQueue<ShareOrder> limitSellShareOrderQueue, ShareOrder sell, ShareOrder buy, SwapProcess swapProcess) throws InterruptedException {
        swapProcess.setVolume(sell.getVolume());
        swapProcess.setPrice(sell.getPrice());
        swapProcess.setLot(sell.getLot());
        Trader traderSell = traderRepository.findById(sell.getTrader().getTraderId()).get();
        traderSell.setCurrentHaveLot(traderSell.getCurrentHaveLot().subtract(sell.getLot()));
        traderRepository.save(traderSell);

        Trader traderBuy = traderRepository.findById(buy.getTrader().getTraderId()).get();
        traderBuy.setCurrentHaveLot(traderBuy.getCurrentHaveLot().add(sell.getLot()));
        traderBuy.setHaveLot(traderBuy.getHaveLot().add(sell.getLot()));
        traderRepository.save(traderBuy);
        this.swapProcess(sell, buy, swapProcess, traderSell, traderBuy);

        buy.setLot(buy.getLot().subtract(sell.getLot()));
        buy.setVolume(buy.getVolume().subtract(swapProcess.getVolume()));
        sell.setLot(BigDecimal.ZERO);
        shareOrderRepository.delete(sell);
        limitSellShareOrderQueue.take();
    }

    private void swapProcess(ShareOrder sell, ShareOrder buy, SwapProcess swapProcess, Trader traderSell, Trader traderBuy) {
        swapProcess.setBuyer(buy.getTrader().getName());
        swapProcess.setSeller(sell.getTrader().getName());
        swapProcess.setTransactionTime(LocalDateTime.now());
        swapProcessRepository.save(swapProcess);

        this.sellProcessEnd(sell, traderSell);
        this.buyProcessEnd(buy, traderBuy);
        log.info("Gerçekleşen işlem : Alan :{} - Satan :{}", buy.getTrader().getName(), sell.getTrader().getName());
    }

    public void buyProcessEnd(ShareOrder buy, Trader traderBuy) {
        if (buy.getShareOrderType().equals(ShareOrderType.MARKET.name())){
            traderBuy.setBalance(traderBuy.getBalance().subtract(buy.getVolume()));
        }
        traderBuy.setCost(shareOrderUtil.costCalculate(traderBuy, buy));
        traderRepository.save(traderBuy);
    }

    public void sellProcessEnd(ShareOrder sell, Trader traderSell) {
        traderSell.setBalance(traderSell.getBalance().add(sell.getVolume()));
        traderRepository.save(traderSell);
    }
}
