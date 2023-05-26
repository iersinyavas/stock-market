package com.artsoft.stock.service;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.exception.InsufficientLotException;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.request.ShareOrderRequest;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareOrderService extends BaseService{

    private final TraderRepository traderRepository;
    private final ShareRepository shareRepository;
    private final ShareOrderRepository shareOrderRepository;
    private final StockMarketService stockMarketService;

    @Transactional
    public void createShareOrderOpenSession(Share share, Long traderId) throws InterruptedException {
        Thread.sleep(100);
        Trader trader = traderRepository.findById(traderId).get();
        ShareOrder shareOrder = new ShareOrder();
        shareOrder.setTrader(trader);
        shareOrder.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        shareOrder.setPrice(RandomData.randomShareOrderPrice(share.getMinPrice(), share.getMaxPrice()));
        shareOrder.setShareOrderStatus(RandomData.shareOrderStatus().name());

        //Açılış seansı olduğu için
        shareOrder.setShareOrderType(ShareOrderType.LIMIT.name()); //shareOrder.setShareOrderType(RandomData.shareOrderType().toString());
        if (shareOrder.getPrice().compareTo(trader.getCost()) < 0 || (shareOrder.getPrice().compareTo(trader.getCost()) == 0 && shareOrder.getShareOrderStatus().equals(ShareOrderStatus.BUY))){
            if (share.getPriceStep().getPrice().compareTo(shareOrder.getPrice()) <= 0){
                shareOrder.setPrice(share.getPriceStep().getPrice());
            }
            this.processBuy(trader, shareOrder);
        } else if (shareOrder.getPrice().compareTo(trader.getCost()) > 0 || shareOrder.getPrice().compareTo(trader.getCost()) == 0){
            if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.BUY)){
                if (share.getPriceStep().getPrice().compareTo(shareOrder.getPrice()) <= 0){
                    shareOrder.setPrice(share.getPriceStep().getPrice());
                }
                this.processBuy(trader, shareOrder);
            }else {
                if (share.getPriceStep().getPrice().compareTo(shareOrder.getPrice()) >= 0){
                    shareOrder.setPrice(share.getPriceStep().getPrice());
                }
                this.processSell(trader, shareOrder);
            }
        }

        this.saveProcessEntity(share, trader, shareOrder);
    }

    @Transactional
    public void createShareOrder(Share share, Long traderId) throws InterruptedException {
        Thread.sleep(1000);
        Trader trader = traderRepository.findById(traderId).get();

        ShareOrder shareOrder = new ShareOrder();
        shareOrder.setTrader(trader);
        shareOrder.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        shareOrder.setPrice(RandomData.randomShareOrderPrice(share.getMinPrice(), share.getMaxPrice()));
        shareOrder.setShareOrderStatus(RandomData.shareOrderStatus().name());
        shareOrder.setShareOrderType(ShareOrderType.LIMIT.name());
        CONTROL:{
            if (shareOrder.getPrice().compareTo(trader.getCost()) < 0 || (shareOrder.getPrice().compareTo(trader.getCost()) == 0 && shareOrder.getShareOrderStatus().equals(ShareOrderStatus.BUY))){
                if (share.getPriceStep().getPrice().compareTo(shareOrder.getPrice()) <= 0){
                    shareOrder.setShareOrderType(RandomData.shareOrderType().name());
                    shareOrder.setShareOrderStatus(ShareOrderStatus.BUY.name());
                    if (shareOrder.getShareOrderType().equals(ShareOrderType.MARKET.name())){
                        shareOrder.setPrice(null);
                        shareOrder.setLot(RandomData.randomLot(trader.getBalance().divide(share.getPrice(), 2, RoundingMode.FLOOR)));
                        break CONTROL;
                    }
                    shareOrder.setPrice(share.getPriceStep().getPrice());
                }
                this.processBuy(trader, shareOrder);
            } else if (shareOrder.getPrice().compareTo(trader.getCost()) > 0 || shareOrder.getPrice().compareTo(trader.getCost()) == 0){
                if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.BUY.name())){
                    if (share.getPriceStep().getPrice().compareTo(shareOrder.getPrice()) <= 0){
                        shareOrder.setShareOrderType(RandomData.shareOrderType().name());
                        if (shareOrder.getShareOrderType().equals(ShareOrderType.MARKET.name())){
                            shareOrder.setPrice(null);
                            shareOrder.setLot(RandomData.randomLot(trader.getBalance().divide(share.getPrice(), 2, RoundingMode.FLOOR)));
                            break CONTROL;
                        }
                        shareOrder.setPrice(share.getPriceStep().getPrice());
                    }
                    this.processBuy(trader, shareOrder);
                }else { //SELL
                    if (trader.getHaveLot().compareTo(BigDecimal.ZERO) == 0){
                        return;
                    }
                    if (share.getPriceStep().getPrice().compareTo(shareOrder.getPrice()) >= 0){
                        shareOrder.setShareOrderType(RandomData.shareOrderType().name());
                        shareOrder.setShareOrderStatus(ShareOrderStatus.SELL.name());
                        if (shareOrder.getShareOrderType().equals(ShareOrderType.MARKET.name())){
                            shareOrder.setPrice(null);
                            shareOrder.setLot(RandomData.randomLot(trader.getHaveLot()));
                            trader.setHaveLot(trader.getHaveLot().subtract(shareOrder.getLot()));
                            break CONTROL;
                        }
                        shareOrder.setPrice(share.getPriceStep().getPrice());
                    }
                    this.processSell(trader, shareOrder);
                }
            }
        }
        this.saveProcessEntity(share, trader, shareOrder);
    }

    @Transactional
    public ShareOrder createShareOrder(ShareOrderRequest shareOrderRequest) throws InsufficientLotException, InsufficientBalanceException {
        ShareOrder shareOrder = new ShareOrder();
        Trader trader = traderRepository.findById(shareOrderRequest.getTraderId()).get();
        if (shareOrderRequest.getShareOrderStatus().equals(ShareOrderStatus.SELL.name()) && trader.getCurrentHaveLot().compareTo(shareOrderRequest.getLot()) < 0){
            log.info("Yetersiz lot...");
            throw new InsufficientLotException();
        }
        shareOrder.setTrader(trader);
        shareOrder.setPrice(shareOrderRequest.getPrice());
        shareOrder.setLot(shareOrderRequest.getLot());
        shareOrder.setVolume(shareOrder.getPrice().multiply(shareOrder.getLot()));
        if (shareOrderRequest.getShareOrderStatus().equals(ShareOrderStatus.BUY) &&
                shareOrderRequest.getShareOrderType().equals(ShareOrderType.LIMIT.name()) &&
                trader.getBalance().compareTo(shareOrder.getVolume()) < 0){
            throw new InsufficientBalanceException();
        }
        shareOrder.setShareOrderStatus(shareOrderRequest.getShareOrderStatus());
        shareOrder.setShareOrderType(shareOrderRequest.getShareOrderType());
        shareOrder.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));

        return shareOrderRepository.save(shareOrder);
    }

    private void saveProcessEntity(Share share, Trader trader, ShareOrder shareOrder) throws InterruptedException {
        traderRepository.save(trader);
        shareOrderRepository.save(shareOrder);
        stockMarketService.sendShareOrderToStockMarket(share, shareOrder);
    }

    private void processSell(Trader trader, ShareOrder shareOrder) {
        shareOrder.setShareOrderStatus(ShareOrderStatus.SELL.name());
        shareOrder.setLot(RandomData.randomLot(trader.getHaveLot()));
        shareOrder.setVolume(shareOrder.getLot().multiply(shareOrder.getPrice()));
        trader.setHaveLot(trader.getHaveLot().subtract(shareOrder.getLot()));
    }

    private void processBuy(Trader trader, ShareOrder shareOrder) {
        shareOrder.setShareOrderStatus(ShareOrderStatus.BUY.name());
        shareOrder.setLot(RandomData.randomLot(trader.getBalance().divide(shareOrder.getPrice(), 2, RoundingMode.FLOOR)));
        shareOrder.setVolume(shareOrder.getLot().multiply(shareOrder.getPrice()));
        trader.setBalance(trader.getBalance().subtract(shareOrder.getVolume()));
    }

    public void delete(ShareOrder shareOrder){
        shareOrderRepository.delete(shareOrder);
    }
}
