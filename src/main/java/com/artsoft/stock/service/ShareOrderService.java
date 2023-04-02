package com.artsoft.stock.service;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.util.GeneralEnumeration;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class ShareOrderService {

    @Autowired
    private TraderRepository traderRepository;
    @Autowired
    private ShareRepository shareRepository;
    @Autowired
    private ShareOrderRepository shareOrderRepository;

    public void createShareOrderForOpenSession() throws InterruptedException {
        Random random = new Random();
        Share share = shareRepository.findById(1L).get();
        List<Long> traderIdList = traderRepository.getTraderListForOpenSession(share.getCurrentSellPrice());

        BlockingQueue<Long> traderIdQueue = new LinkedBlockingQueue<>(traderIdList);
        List<Long> traderIdForShareOrder = new ArrayList<>();

        while (traderIdQueue.size() != 0){
            Long traderId = traderIdQueue.take();
            if (random.nextInt(2) == 1){
                traderIdForShareOrder.add(traderId);
            }
        }

        List<Trader> traderList = traderRepository.getTraderListByTraderId(traderIdForShareOrder, share.getCurrentSellPrice());
        BlockingQueue<Trader> traderQueue = new LinkedBlockingQueue<>(traderList);

        while (traderQueue.size() != 0){
            Trader trader = traderRepository.findById(traderQueue.take().getTraderId()).get();
            ShareOrder shareOrder = new ShareOrder();
            shareOrder.setTrader(trader);
            shareOrder.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
            shareOrder.setPrice(RandomData.randomShareOrderPrice(share.getMinPrice(), share.getMaxPrice()));

            //Açılış seansı olduğu için
            shareOrder.setShareOrderType("LIMIT"); //shareOrder.setShareOrderType(RandomData.shareOrderType().toString());
            if (shareOrder.getPrice().compareTo(trader.getCost()) <= 0 || shareOrder.getPrice().compareTo(share.getCurrentSellPrice()) <= 0){
                if ((shareOrder.getPrice().compareTo(trader.getCost()) >= 0 && shareOrder.getPrice().compareTo(share.getCurrentSellPrice()) < 0) || (shareOrder.getPrice().compareTo(trader.getCost()) <= 0 && shareOrder.getPrice().compareTo(share.getCurrentSellPrice()) >= 0)){
                    shareOrder.setPrice(share.getCurrentBuyPrice());
                }
                this.processBuy(trader, shareOrder);
            } else{
                shareOrder.setShareOrderStatus(RandomData.shareOrderStatus().toString());
                if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.BUY)){
                    this.processBuy(trader, shareOrder);
                }else {
                    this.processSell(trader, shareOrder);
                }
            }
            traderRepository.save(trader);
            shareOrderRepository.save(shareOrder);
            log.info("Gönderilen Emir : {}", shareOrder);
        }
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

    public void createShareOrder() throws InterruptedException {
        Share share = shareRepository.findById(1L).get();
        ShareOrder shareOrder = new ShareOrder();
        shareOrder.setPrice(RandomData.randomShareOrderPrice(share.getMinPrice(), share.getCurrentSellPrice()));
    }
}
