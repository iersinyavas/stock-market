package com.artsoft.stock.service;

import com.artsoft.stock.dto.ShareOrderSummaryInfoForMatchDTO;
import com.artsoft.stock.dto.ShareSummaryInfoTransport;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareOrderRepository;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
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

    public void matchShareOrderForOpenSession() throws InterruptedException {
        List<Long> deleteShareOrderId = new ArrayList<>();
        ShareSummaryInfoTransport shareSummaryInfoTransport = this.setOpenPriceShare();
        List<ShareOrder> shareOrderListForOpenSession = shareOrderRepository.getShareOrderListForOpenSession(shareSummaryInfoTransport.getCurrentSellPrice(), shareSummaryInfoTransport.getCurrentBuyPrice());
        Map<String, List<ShareOrder>> sellOrBuyShareOrderMap = shareOrderListForOpenSession.stream().collect(Collectors.groupingBy(ShareOrder::getShareOrderStatus));
        List<ShareOrder> sortedSellShareOrderList = sellOrBuyShareOrderMap.get("SELL").stream().sorted(Comparator.comparing(ShareOrder::getPrice)).collect(Collectors.toList());
        List<ShareOrder> sortedBuyShareOrderList = sellOrBuyShareOrderMap.get("BUY").stream().sorted(Comparator.comparing(ShareOrder::getPrice).reversed()).collect(Collectors.toList());
        BlockingQueue<ShareOrder> sellShareOrderQueue = new LinkedBlockingQueue<>(sortedSellShareOrderList);
        BlockingQueue<ShareOrder> buyShareOrderQueue = new LinkedBlockingQueue<>(sortedBuyShareOrderList);

        while(!sellShareOrderQueue.isEmpty() && !buyShareOrderQueue.isEmpty()){
            ShareOrder sell = sellShareOrderQueue.peek();
            ShareOrder buy = buyShareOrderQueue.peek();

            if (sell.getLot().compareTo(buy.getPrice()) == 0){
                Trader traderSell = traderRepository.findById(sell.getTrader().getTraderId()).get();
                traderSell.setTotalAmount(traderSell.getTotalAmount().subtract(sell.getLot().multiply(traderSell.getCost())));
                traderSell.setHaveLot(traderSell.getHaveLot().subtract(sell.getLot()));
                traderSell.setBalance(traderSell.getBalance().add(sell.getLot().multiply(sell.getPrice())));
                traderSell.setCost(traderSell.getTotalAmount().divide(traderSell.getHaveLot()));
                traderRepository.save(traderSell);
                deleteShareOrderId.add(sell.getShareOrderId());

                Trader traderBuy = traderRepository.findById(buy.getTrader().getTraderId()).get();
                traderBuy.setTotalAmount(traderBuy.getTotalAmount().add(buy.getLot().multiply(buy.getPrice())));
                traderBuy.setHaveLot(traderBuy.getHaveLot().add(buy.getLot()));
                traderBuy.setBalance(traderBuy.getBalance().subtract(buy.getLot().multiply(buy.getPrice())));
                traderBuy.setCost(traderBuy.getTotalAmount().divide(traderBuy.getHaveLot()));
                traderRepository.save(traderBuy);
                deleteShareOrderId.add(buy.getShareOrderId());
                sellShareOrderQueue.take();
                buyShareOrderQueue.take();
                continue;
            }

            if (sell.getLot().compareTo(buy.getPrice()) < 0){
                Trader trader = traderRepository.findById(sell.getTrader().getTraderId()).get();
                trader.setTotalAmount(trader.getTotalAmount().subtract(sell.getLot().multiply(trader.getCost())));
                trader.setHaveLot(trader.getHaveLot().subtract(sell.getLot()));
                trader.setBalance(trader.getBalance().add(sell.getLot().multiply(sell.getPrice())));
                trader.setCost(trader.getTotalAmount().divide(trader.getHaveLot()));
                traderRepository.save(trader);
                buy.setLot(buy.getLot().subtract(sell.getLot()));
                deleteShareOrderId.add(sell.getShareOrderId());
                sellShareOrderQueue.take();
                continue;
            }else {
                Trader trader = traderRepository.findById(buy.getTrader().getTraderId()).get();
                trader.setTotalAmount(trader.getTotalAmount().add(buy.getLot().multiply(buy.getPrice())));
                trader.setHaveLot(trader.getHaveLot().add(buy.getLot()));
                trader.setBalance(trader.getBalance().subtract(buy.getLot().multiply(buy.getPrice())));
                trader.setCost(trader.getTotalAmount().divide(trader.getHaveLot()));
                traderRepository.save(trader);
                sell.setLot(sell.getLot().subtract(buy.getLot()));
                deleteShareOrderId.add(buy.getShareOrderId());
                buyShareOrderQueue.take();
            }
        }
        shareOrderRepository.deleteAllByIdList(deleteShareOrderId);
        log.info("Açılış seansı sona erdi.");


    }

    public void matchShareOrder() throws InterruptedException {
        Share share = shareRepository.findById(1L).get();
        traderRepository.getTraderListWantOnlyBuy(share.getCurrentSellPrice());
    }

    private ShareSummaryInfoTransport setOpenPriceShare(){
        int difference;
        Share share;
        while (true){
            share = shareRepository.findById(1L).get();
            Map<String, BigDecimal> summaryInfoForMatchMap = this.findDifference(share);
            if (!summaryInfoForMatchMap.containsKey("SELL") || !summaryInfoForMatchMap.containsKey("BUY")){
                if (!summaryInfoForMatchMap.containsKey("SELL")){
                    share.setCurrentSellPrice(share.getCurrentSellPrice().add(BigDecimal.valueOf(0.01)));
                    share.setCurrentBuyPrice(share.getCurrentBuyPrice().add(BigDecimal.valueOf(0.01)));
                    shareRepository.save(share);
                }else {
                    share.setCurrentSellPrice(share.getCurrentSellPrice().subtract(BigDecimal.valueOf(0.01)));
                    share.setCurrentBuyPrice(share.getCurrentBuyPrice().subtract(BigDecimal.valueOf(0.01)));
                    shareRepository.save(share);
                }
                if (share.getCurrentBuyPrice().compareTo(share.getMaxPrice()) == 0 || share.getCurrentSellPrice().compareTo(share.getMinPrice()) == 0){
                    shareRepository.save(share);
                    break;
                }
                continue;
            }

            if (summaryInfoForMatchMap.get("SELL").intValue() > summaryInfoForMatchMap.get("BUY").intValue()){
                difference = summaryInfoForMatchMap.get("SELL").subtract(summaryInfoForMatchMap.get("BUY")).intValue();
                share.setCurrentSellPrice(share.getCurrentSellPrice().subtract(BigDecimal.valueOf(0.01)));
                share.setCurrentBuyPrice(share.getCurrentBuyPrice().subtract(BigDecimal.valueOf(0.01)));
                shareRepository.save(share);
                summaryInfoForMatchMap = this.findDifference(share);
                if (this.keyControl(summaryInfoForMatchMap)){
                    difference = summaryInfoForMatchMap.get("SELL").subtract(summaryInfoForMatchMap.get("BUY")).intValue();
                }
                if (difference < 0){
                    share.setCurrentSellPrice(share.getCurrentSellPrice().add(BigDecimal.valueOf(0.01)));
                    share.setCurrentBuyPrice(share.getCurrentBuyPrice().add(BigDecimal.valueOf(0.01)));
                    shareRepository.save(share);
                    break;
                }
            }else if (summaryInfoForMatchMap.get("SELL").intValue() == summaryInfoForMatchMap.get("BUY").intValue()){
                shareRepository.save(share);
                break;
            }else {
                difference = summaryInfoForMatchMap.get("SELL").subtract(summaryInfoForMatchMap.get("BUY")).intValue();
                share.setCurrentSellPrice(share.getCurrentSellPrice().add(BigDecimal.valueOf(0.01)));
                share.setCurrentBuyPrice(share.getCurrentBuyPrice().add(BigDecimal.valueOf(0.01)));
                shareRepository.save(share);
                summaryInfoForMatchMap = this.findDifference(share);
                if (this.keyControl(summaryInfoForMatchMap)){
                    difference = summaryInfoForMatchMap.get("SELL").subtract(summaryInfoForMatchMap.get("BUY")).intValue();
                }
                if (difference > 0){
                    share.setCurrentSellPrice(share.getCurrentSellPrice().subtract(BigDecimal.valueOf(0.01)));
                    share.setCurrentBuyPrice(share.getCurrentBuyPrice().subtract(BigDecimal.valueOf(0.01)));
                    shareRepository.save(share);
                    break;
                }
            }
        }
        ShareSummaryInfoTransport shareSummaryInfoTransport = new ShareSummaryInfoTransport();
        shareSummaryInfoTransport.setCurrentBuyPrice(share.getCurrentBuyPrice());
        shareSummaryInfoTransport.setCurrentSellPrice(share.getCurrentSellPrice());
        log.info("Alış : {}, Satış : {}", shareSummaryInfoTransport.getCurrentBuyPrice(), shareSummaryInfoTransport.getCurrentSellPrice());
        return shareSummaryInfoTransport;
    }

    private Map<String, BigDecimal> findDifference(Share share) {
        List<ShareOrderSummaryInfoForMatchDTO> summaryInfoForMatchList = shareOrderRepository.getSummaryInfoForMatch(share.getCurrentSellPrice(), share.getCurrentBuyPrice());
        return summaryInfoForMatchList.stream().collect(Collectors.toMap(ShareOrderSummaryInfoForMatchDTO::getShareOrderStatus, ShareOrderSummaryInfoForMatchDTO::getLot));
    }

    private boolean keyControl(Map<String, BigDecimal> summaryInfoForMatchMap){
        if (!summaryInfoForMatchMap.containsKey("SELL") || !summaryInfoForMatchMap.containsKey("BUY")){
            return false;
        }
        return true;
    }
}
