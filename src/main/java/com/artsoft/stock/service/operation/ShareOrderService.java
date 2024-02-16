package com.artsoft.stock.service.operation;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.exception.InsufficientBalanceException;
import com.artsoft.stock.exception.InsufficientLotException;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.request.ShareOrderRequest;
import com.artsoft.stock.constant.GeneralEnumeration.*;
import com.artsoft.stock.service.BaseService;
import com.artsoft.stock.service.TraderService;
import com.artsoft.stock.service.broker.StockMarketService;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.TraderBehavior;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareOrderService extends BaseService {

    private final TraderRepository traderRepository;
    private final ShareOrderRepository shareOrderRepository;
    private final StockMarketService stockMarketService;
    private final TraderService traderService;
    private Random random = new Random();

    @Transactional
    public void createShareOrderOpenSession(Share share, Long traderId) throws InterruptedException {
        Trader trader = traderRepository.findById(traderId).get();
        ShareOrder shareOrder = new ShareOrder();
        shareOrder.setTrader(trader);
        shareOrder.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        shareOrder.setPrice(traderService.selectPrice());
        //Açılış seansı olduğu için
        shareOrder.setShareOrderType(ShareOrderType.LIMIT.name());
        if (trader.getTraderBehavior().equals(TraderBehavior.BUYER.name())){
            if (trader.getBalance().compareTo(share.getPriceStep().getPrice()) < 0){
                log.info("Yetersiz bakiye...");
                return;
            }
            if (share.getPriceStep().getPrice().compareTo(shareOrder.getPrice()) <= 0){
                shareOrder.setPrice(share.getPriceStep().getPrice());
            }
            this.processBuy(trader, shareOrder);
        } else {
            if (trader.getHaveLot().compareTo(BigDecimal.ZERO) == 0){
                log.info("Yetersiz lot...");
                return;
            }
            if (share.getPriceStep().getPrice().compareTo(shareOrder.getPrice()) >= 0){
                shareOrder.setPrice(share.getPriceStep().getPrice());
            }
            this.processSell(trader, shareOrder);
        }

    }

    @Transactional
    public void createShareOrder(Share share, Long traderId) throws InterruptedException {
        Trader trader = traderRepository.findById(traderId).get();

        ShareOrder shareOrder = new ShareOrder();
        shareOrder.setTrader(trader);
        shareOrder.setCreateTime(LocalDateTime.now());
        shareOrder.setPrice(traderService.selectPrice());
        shareOrder.setShareOrderType(RandomData.shareOrderType().name());


    }

    @Transactional
    public ShareOrder createShareOrder(ShareOrderRequest shareOrderRequest) throws InsufficientLotException, InsufficientBalanceException {
        ShareOrder shareOrder = new ShareOrder();
        shareOrder.setShareOrderStatus(shareOrderRequest.getShareOrderStatus());
        shareOrder.setShareOrderType(shareOrderRequest.getShareOrderType());
        Trader trader = traderRepository.findById(shareOrderRequest.getTraderId()).get();
        shareOrder.setTrader(trader);
        shareOrder.setPrice(shareOrderRequest.getPrice());
        shareOrder.setLot(shareOrderRequest.getLot());
        shareOrder.setVolume(shareOrder.getPrice().multiply(shareOrder.getLot()));
        if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.BUY.name())){
            if (shareOrder.getShareOrderType().equals(ShareOrderType.MARKET.name())){
                shareOrder.setPrice(null);
                shareOrder.setVolume(null);
            }else {//LIMIT
                if (trader.getBalance().compareTo(shareOrder.getVolume()) < 0){
                    log.info("Yetersiz bakiye...");
                    throw new InsufficientBalanceException();
                }
                trader.setBalance(trader.getBalance().subtract(shareOrder.getVolume()));
                traderRepository.save(trader);
            }
        }else {//SELL
            if (trader.getHaveLot().compareTo(shareOrder.getLot()) < 0){
                log.info("Yetersiz lot...");
                throw new InsufficientLotException();
            }
            if (shareOrder.getShareOrderType().equals(ShareOrderType.MARKET.name())){
                shareOrder.setPrice(null);
                shareOrder.setVolume(null);
            }
            trader.setHaveLot(trader.getHaveLot().subtract(shareOrder.getLot()));
            traderRepository.save(trader);
        }
        shareOrder.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        return shareOrderRepository.save(shareOrder);
    }

    private void sendShareOrder(Trader trader, ShareOrder shareOrder) throws InterruptedException {
        traderRepository.save(trader);
        shareOrderRepository.save(shareOrder);
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

    public void sendShareOrder(ShareOrder shareOrder){
        shareOrderRepository.save(shareOrder);
    }

}
