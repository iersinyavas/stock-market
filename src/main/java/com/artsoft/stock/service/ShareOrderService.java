package com.artsoft.stock.service;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.util.GeneralEnumeration.*;
import com.artsoft.stock.util.RandomData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        List<Long> traderIdList = traderRepository.getTraderListForOpenSession();

        BlockingQueue<Long> traderIdQueue = new LinkedBlockingQueue<>(traderIdList);
        List<Long> traderIdForShareOrder = new ArrayList<>();

        while (traderIdQueue.size() != 0){
            Long traderId = traderIdQueue.take();
            if (random.nextInt(2) == 1){
                traderIdForShareOrder.add(traderId);
            }
        }

        List<Trader> traderList = traderRepository.getTraderListByTraderId(traderIdForShareOrder);
        BlockingQueue<Trader> traderQueue = new LinkedBlockingQueue<>(traderList);
        Share share = shareRepository.findById(1L).get();

        while (traderQueue.size() != 0){
            Trader trader = traderRepository.findById(traderQueue.take().getTraderId()).get();
            ShareOrder shareOrder = new ShareOrder();
            shareOrder.setTrader(trader);
            shareOrder.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
            shareOrder.setShareOrderStatus(RandomData.shareOrderStatus().toString());

            //Açılış seansı olduğu için
            shareOrder.setShareOrderType("LIMIT"); //shareOrder.setShareOrderType(RandomData.shareOrderType().toString());
            if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.BUY)){
                shareOrder.setPrice(RandomData.randomShareOrderPrice(share.getMinPrice(), share.getMaxPrice()));
                shareOrder.setLot(RandomData.randomLot(trader.getBalance().divide(share.getCurrentSellPrice())));
                shareOrder.setVolume(shareOrder.getLot().multiply(shareOrder.getPrice()));
                trader.setBalance(trader.getBalance().subtract(shareOrder.getVolume()));
                trader.setTotalAmount(shareOrder.getVolume());
            }else {
                if (trader.getHaveLot().compareTo(BigDecimal.ZERO) <= 0){
                    continue;
                }
                shareOrder.setLot(RandomData.randomLot(trader.getHaveLot()));
                shareOrder.setPrice(RandomData.randomShareOrderPrice(trader.getCost(), share.getMaxPrice()));
                shareOrder.setVolume(shareOrder.getLot().multiply(shareOrder.getPrice()));
                trader.setHaveLot(trader.getHaveLot().subtract(shareOrder.getLot()));
            }
            traderRepository.save(trader);
            shareOrderRepository.save(shareOrder);
            log.info("Gönderilen Emir : {}", shareOrder);
        }
    }

    public void createShareOrder() throws InterruptedException {
        Share share = shareRepository.findById(1L).get();
    }
}
