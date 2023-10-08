package com.artsoft.stock.service;

import com.artsoft.stock.constant.GeneralEnumeration;
import com.artsoft.stock.dto.SwapProcessDTO;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.SwapProcess;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.mapper.SwapProcessMapper;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.util.ShareOrderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationService {

    protected final TraderRepository traderRepository;
    protected final ShareOrderRepository shareOrderRepository;
    protected final ShareOrderUtil shareOrderUtil;
    protected final CandleStickService candleStickService;

    protected void deleteShareOrder(BlockingQueue<ShareOrder> shareOrderQueue, ShareOrder shareOrder) throws InterruptedException {
        shareOrderQueue.take();
        shareOrderRepository.delete(shareOrder);
    }

    protected void balanceControl(ShareOrder buy, ShareOrder sell) throws InsufficientBalanceException {//Kuyruktan geldiği için buy ===> shareOrder oldu
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

    protected void ifSellEqualsBuy(BlockingQueue<ShareOrder> limitSellShareOrderQueue, BlockingQueue<ShareOrder> limitBuyShareOrderQueue, ShareOrder sell, ShareOrder buy, SwapProcess swapProcess) throws InterruptedException {
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

    protected void ifSellGreaterThanBuy(BlockingQueue<ShareOrder> limitBuyShareOrderQueue, ShareOrder sell, ShareOrder buy, SwapProcess swapProcess) throws InterruptedException {
        swapProcess.setVolume(buy.getVolume());
        swapProcess.setPrice(buy.getPrice());
        swapProcess.setLot(buy.getLot());
        Trader traderSell = traderRepository.findById(sell.getTrader().getTraderId()).get();
        traderSell.setCurrentHaveLot(traderSell.getCurrentHaveLot().subtract(buy.getLot()));
        traderRepository.save(traderSell);

        Trader traderBuy = traderRepository.findById(buy.getTrader().getTraderId()).get();
        traderBuy.setHaveLot(traderBuy.getHaveLot().add(buy.getLot()));
        //traderBuy.setCost(shareOrderUtil.costCalculate(traderBuy, buy)); //cost hesabını gözden geçir yanlış hesaplıyor
        traderBuy.setCurrentHaveLot(traderBuy.getCurrentHaveLot().add(buy.getLot()));
        traderRepository.save(traderBuy);
        this.swapProcess(sell, buy, swapProcess, traderSell, traderBuy);

        sell.setLot(sell.getLot().subtract(buy.getLot()));
        sell.setVolume(sell.getVolume().subtract(swapProcess.getVolume()));
        buy.setLot(BigDecimal.ZERO);
        shareOrderRepository.delete(buy);
        limitBuyShareOrderQueue.take();
    }

    protected void ifBuyGreaterThanSell(BlockingQueue<ShareOrder> limitSellShareOrderQueue, ShareOrder sell, ShareOrder buy, SwapProcess swapProcess) throws InterruptedException {
        swapProcess.setVolume(sell.getVolume());
        swapProcess.setPrice(sell.getPrice());
        swapProcess.setLot(sell.getLot());
        Trader traderSell = traderRepository.findById(sell.getTrader().getTraderId()).get();
        traderSell.setCurrentHaveLot(traderSell.getCurrentHaveLot().subtract(sell.getLot()));
        traderRepository.save(traderSell);

        Trader traderBuy = traderRepository.findById(buy.getTrader().getTraderId()).get();
        traderBuy.setHaveLot(traderBuy.getHaveLot().add(sell.getLot()));
        //traderBuy.setCost(shareOrderUtil.costCalculate(traderBuy, buy)); //cost hesabını gözden geçir yanlış hesaplıyor
        traderBuy.setCurrentHaveLot(traderBuy.getCurrentHaveLot().add(sell.getLot()));
        traderRepository.save(traderBuy);
        this.swapProcess(sell, buy, swapProcess, traderSell, traderBuy);

        buy.setLot(buy.getLot().subtract(sell.getLot()));
        buy.setVolume(buy.getVolume().subtract(swapProcess.getVolume()));
        sell.setLot(BigDecimal.ZERO);
        shareOrderRepository.delete(sell);
        limitSellShareOrderQueue.take();
    }

    public void swapProcess(ShareOrder sell, ShareOrder buy, SwapProcess swapProcess, Trader traderSell, Trader traderBuy) {

        this.sellProcessEnd(sell, traderSell);
        this.buyProcessEnd(buy, traderBuy);

        swapProcess.setBuyer(buy.getTrader().getName());
        swapProcess.setSeller(sell.getTrader().getName());

        SwapProcessDTO swapProcessDTO = SwapProcessMapper.INSTANCE.entityToDTO(swapProcess);

        candleStickService.saveAndSendSwapProcess(swapProcess, swapProcessDTO, null);
        log.info("Gerçekleşen işlem : Alan :{} - Satan :{} Hisse Adedi: {} Hacim: {} İşlem Fiyatı: {}", buy.getTrader().getName(), sell.getTrader().getName(), swapProcessDTO.getLot(), swapProcessDTO.getVolume(), swapProcessDTO.getPrice());
    }



    public void buyProcessEnd(ShareOrder buy, Trader traderBuy) {
        if (buy.getShareOrderType().equals(GeneralEnumeration.ShareOrderType.MARKET.name())){
            traderBuy.setBalance(traderBuy.getBalance().subtract(buy.getVolume()));
        }
        traderRepository.save(traderBuy);
    }

    public void sellProcessEnd(ShareOrder sell, Trader traderSell) {
        traderSell.setBalance(traderSell.getBalance().add(sell.getVolume()));
        //traderSell.setCostAmount(traderSell.getCostAmount().subtract(traderSell.getCost().multiply(sell.getLot())));
        traderRepository.save(traderSell);
    }

    public void setPrice(Share share, BlockingQueue<ShareOrder> limitSellShareOrderQueue, BlockingQueue<ShareOrder> limitBuyShareOrderQueue) {
        if (limitSellShareOrderQueue.isEmpty() && limitBuyShareOrderQueue.isEmpty()){
            share.setPriceStep(share.getPriceStep().priceDown(share.getPriceStep()));
        } else if (limitSellShareOrderQueue.isEmpty()){
            share.setPriceStep(share.getPriceStep().priceUp(share.getPriceStep()));
        } else {
            share.setPriceStep(share.getPriceStep().priceDown(share.getPriceStep()));
        }
        share.setMarketValue(share.getPrice().multiply(share.getCurrentLot()));
        share.setMarketBookRatio(share.getMarketValue().divide(share.getOwnResources(), 2, RoundingMode.FLOOR));
        candleStickService.saveAndSendSwapProcess(null, null, share);
    }
}
