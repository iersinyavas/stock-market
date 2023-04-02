package com.artsoft.stock.service;

import com.artsoft.stock.dto.ShareOrderSummaryInfoForMatchDTO;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.util.ShareOrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StockMarketService {

    @Autowired
    private TraderRepository traderRepository;
    @Autowired
    private ShareOrderRepository shareOrderRepository;
    @Autowired
    private ShareRepository shareRepository;
    @Autowired
    private ShareOrderUtil shareOrderUtil;

    public void matchShareOrderForOpenSession() throws InterruptedException {
        List<ShareOrder> deleteShareOrderList = new ArrayList<>();
        List<ShareOrderSummaryInfoForMatchDTO> processSell = shareOrderRepository.getSummaryInfoForSellMatch();
        List<ShareOrderSummaryInfoForMatchDTO> processBuy = shareOrderRepository.getSummaryInfoForBuyMatch();

        if (processBuy.get(0).getPrice().compareTo(processSell.get(0).getPrice()) != 0 && processBuy.get(0).getPrice().compareTo(processSell.get(1).getPrice()) != 0){
            return;
        }

        List<ShareOrder> shareOrderListForOpenSession = shareOrderRepository.getShareOrderListForSelectPrice(processBuy.get(0).getPrice());
        Map<String, List<ShareOrder>> sellOrBuyShareOrderMap = shareOrderListForOpenSession.stream().collect(Collectors.groupingBy(ShareOrder::getShareOrderStatus));
        List<ShareOrder> sortedSellShareOrderList = sellOrBuyShareOrderMap.get("SELL").stream().sorted(Comparator.comparing(ShareOrder::getPrice)).collect(Collectors.toList());
        List<ShareOrder> sortedBuyShareOrderList = sellOrBuyShareOrderMap.get("BUY").stream().sorted(Comparator.comparing(ShareOrder::getPrice).reversed()).collect(Collectors.toList());
        BlockingQueue<ShareOrder> sellShareOrderQueue = new LinkedBlockingQueue<>(sortedSellShareOrderList);
        BlockingQueue<ShareOrder> buyShareOrderQueue = new LinkedBlockingQueue<>(sortedBuyShareOrderList);

        while(!sellShareOrderQueue.isEmpty() && !buyShareOrderQueue.isEmpty()){
            ShareOrder sell = sellShareOrderQueue.peek();
            ShareOrder buy = buyShareOrderQueue.peek();

            if (sell.getLot().compareTo(buy.getLot()) < 0){
                //yarısı satılan orderlar için çare düşün
                this.swapProcess(sell, buy);

                buy.setLot(buy.getLot().subtract(sell.getLot()));
                deleteShareOrderList.add(sell);
                sellShareOrderQueue.take();
                continue;
            }else if(sell.getLot().compareTo(buy.getLot()) > 0){
                this.swapProcess(sell, buy);

                sell.setLot(sell.getLot().subtract(buy.getLot()));
                deleteShareOrderList.add(buy);
                buyShareOrderQueue.take();
            }else{
                this.swapProcess(sell, buy);

                deleteShareOrderList.add(sell);
                deleteShareOrderList.add(buy);
                sellShareOrderQueue.take();
                buyShareOrderQueue.take();
            }
        }
        if (!deleteShareOrderList.isEmpty()){
            deleteShareOrderList.stream().forEach(shareOrder -> {
                shareOrderRepository.delete(shareOrder);
            });
        }
        log.info("Açılış seansı sona erdi.");
    }

    private void swapProcess(ShareOrder sell, ShareOrder buy) {
        this.sellProcessEnd(sell);
        this.buyProcessEnd(buy);
        log.info("Gerçekleşen işlem : Alan :{} - Satan :{}", buy.getTrader().getName(), sell.getTrader().getName());
    }

    public void buyProcessEnd(ShareOrder buy) {
        Trader traderBuy = traderRepository.findById(buy.getTrader().getTraderId()).get();
        traderBuy.setHaveLot(traderBuy.getHaveLot().add(buy.getLot()));
        traderBuy.setCost(shareOrderUtil.costCalculate(traderBuy, buy));
        traderRepository.save(traderBuy);
    }

    public void sellProcessEnd(ShareOrder sell) {
        Trader traderSell = traderRepository.findById(sell.getTrader().getTraderId()).get();
        traderSell.setBalance(traderSell.getBalance().add(sell.getLot().multiply(sell.getPrice())));
        traderRepository.save(traderSell);
    }

    public void matchShareOrder() throws InterruptedException {
        Share share = shareRepository.findById(1L).get();
        traderRepository.getTraderListWantOnlyBuy(share.getCurrentSellPrice());
    }

    private boolean keyControl(Map<String, BigDecimal> summaryInfoForMatchMap){
        if (!summaryInfoForMatchMap.containsKey("SELL") || !summaryInfoForMatchMap.containsKey("BUY")){
            return false;
        }
        return true;
    }


}
