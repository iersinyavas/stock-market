package com.artsoft.stock.service;

import com.artsoft.stock.constant.GeneralEnumeration.*;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.ShareOrder;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.request.TraderRequest;
import com.artsoft.stock.service.operation.ShareOrderService;
import com.artsoft.stock.util.BatchUtil;
import com.artsoft.stock.util.PriceStepContext;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.TraderBehavior;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraderService {

    private final TraderRepository traderRepository;
    private final ShareRepository shareRepository;
    private final ShareOrderService shareOrderService;
    private final BatchUtil batchUtil;

    @Value("${share.remaining-balance-day}")
    private Integer remainingBalanceDay;
    Random random = new Random();

    public Trader createTrader(Share share, BigDecimal perPersonShareQuantity){
        String[] name = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","R","S","T","U","V","Y","Z","X","Q","W"};
        Trader trader = new Trader();
        StringBuilder nameBuilder = new StringBuilder();
        for (int i=0; i<random.nextInt(10)+1; i++){
            nameBuilder.append(name[random.nextInt(name.length)]);
        }
        trader.setName(nameBuilder.toString());
        trader.setBalance(new BigDecimal(random.nextInt(491)+10).multiply(share.getPrice()));

        //BigDecimal randomLot = RandomData.randomLot(share.getLot());
        //trader.setHaveLot(randomLot.compareTo(BigDecimal.valueOf(100L)) > 0 ? RandomData.randomLot(BigDecimal.valueOf(100)) : randomLot);
        trader.setHaveLot(perPersonShareQuantity.compareTo(share.getLot()) <= 0 ? perPersonShareQuantity : share.getLot());
        trader.setCurrentHaveLot(trader.getHaveLot());
        trader.setReturnInvestmentRatio(BigDecimal.valueOf(random.nextInt(100)+1));
        trader.setTraderBehavior(TraderBehavior.BUYER.name()/*RandomData.traderBehavior().name()*/);
        trader.setPriceBuy(RandomData.randomShareOrderPrice(share.getPrice(), this.targetPrice(share)));
        trader.setPriceSell(RandomData.randomShareOrderPrice(share.getPrice().divide(BigDecimal.valueOf(2), RoundingMode.FLOOR), share.getPrice()));

        share.setLot(share.getLot().subtract(trader.getHaveLot()));
        log.info("Trader adı : {}", trader.getName());
        return trader;
    }

    public Trader createNewTrader(Share share){
        String[] name = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","R","S","T","U","V","Y","Z","X","Q","W"};
        Trader trader = new Trader();
        StringBuilder nameBuilder = new StringBuilder();
        for (int i=0; i<random.nextInt(10)+1; i++){
            nameBuilder.append(name[random.nextInt(name.length)]);
        }
        trader.setName(nameBuilder.toString());
        trader.setBalance(new BigDecimal(random.nextInt(share.getCurrentLot().intValue())+1).multiply(share.getPriceStep().getPrice()));

        trader.setHaveLot(BigDecimal.ZERO);
        trader.setCurrentHaveLot(BigDecimal.ZERO);
        trader.setReturnInvestmentRatio(BigDecimal.valueOf(random.nextInt(100)+1));
        trader.setTraderBehavior(TraderBehavior.BUYER.name());
        trader.setPriceBuy(RandomData.randomShareOrderPrice(BigDecimal.ZERO,  this.targetPrice(share)));
        trader.setPriceSell(RandomData.randomShareOrderPrice(BigDecimal.ZERO, share.getPrice()));

        log.info("Trader adı : {}", trader.getName());
        return trader;
    }

    private BigDecimal targetPrice(Share share){
        BigDecimal perShareIncome = share.getLastNetProfit().divide(share.getFund(), 2, RoundingMode.FLOOR);
        BigDecimal expectedProfit = share.getTotalInvestmentAmount().multiply(share.getReturnInvestmentAverage().divide(BigDecimal.valueOf(100), 2, RoundingMode.FLOOR));
        BigDecimal expectedPerShareIncome = expectedProfit.multiply(BigDecimal.valueOf(4)).divide(share.getFund(), 2, RoundingMode.FLOOR);
        BigDecimal perShareIncomeRatio = expectedPerShareIncome.divide(perShareIncome, 2, RoundingMode.FLOOR);
        BigDecimal targetFK = perShareIncomeRatio.multiply(share.getPriceIncomeRatio());
        BigDecimal targetPrice = targetFK.multiply(expectedPerShareIncome);
        return targetPrice;
    }

    public BigDecimal updateTargetPrice(Share share){
        Integer remainingBalanceDayForRatio = (remainingBalanceDay - share.getRemainingBalanceDay()) == 0 ? 1 : (remainingBalanceDay - share.getRemainingBalanceDay());
        BigDecimal ratio = BigDecimal.valueOf(remainingBalanceDay).divide(BigDecimal.valueOf(remainingBalanceDayForRatio), 2, RoundingMode.FLOOR);
        BigDecimal profit = share.getProfit().multiply(ratio);
        BigDecimal expected = share.getFund().add(profit);
        BigDecimal targetPrice = expected.multiply(share.getMarketBookRatio()).divide(share.getCurrentLot(), 2, RoundingMode.FLOOR);
        return targetPrice;
    }

    public BlockingQueue<Long> getTraderQueue(Share share) throws InterruptedException {
        Random random = new Random();
        List<Long> traderIdList = this.getAllTraderIdList();

        BlockingQueue<Long> traderIdQueue = new LinkedBlockingQueue<>(traderIdList);
        List<Long> traderIdForShareOrder = new ArrayList<>();

        while (traderIdQueue.size() != 0){
            Long traderId = traderIdQueue.take();
            if (random.nextInt(2) == 1){
                traderIdForShareOrder.add(traderId);
            }
        }

        traderIdList = traderRepository.getTraderIdListByTraderId(traderIdForShareOrder, batchUtil.getTraderId());
        return new LinkedBlockingQueue<>(traderIdList);
    }

    public List<Long> getTraderList(BigDecimal price) throws InterruptedException {
        List<Long> traderIdList = this.getAllTraderIdList();

        BlockingQueue<Long> traderIdQueue = new LinkedBlockingQueue<>(traderIdList);
        List<Long> traderIdForShareOrder = new ArrayList<>();

        while (traderIdQueue.size() != 0){
            Long traderId = traderIdQueue.take();
            if (random.nextInt(2) == 1){
                traderIdForShareOrder.add(traderId);
            }
        }

        return traderRepository.getTraderIdListByTraderId(traderIdForShareOrder, batchUtil.getTraderId());
    }

    public List<Trader> getTraderList() throws InterruptedException {

        List<Long> traderIdList = traderRepository.findAll().stream().map(trader -> trader.getTraderId()).collect(Collectors.toList());

        BlockingQueue<Long> traderIdQueue = new LinkedBlockingQueue<>(traderIdList);
        List<Long> traderIdForShareOrder = new ArrayList<>();

        while (traderIdQueue.size() != 0){
            Long traderId = traderIdQueue.take();
            if (random.nextInt(2) == 1){
                traderIdForShareOrder.add(traderId);
            }
        }

        return traderRepository.getTraderListByTraderId(traderIdForShareOrder);
    }

    public List<Long> getAllTraderIdList(){
        return traderRepository.getTraderListForOpenSession(batchUtil.getTraderId());
    }

    public List<Long> getTraderIdListForShareOrder(BigDecimal price){
        return traderRepository.getTraderListForShareOrder(batchUtil.getTraderId(), price);
    }

    public Trader getTrader(Long traderId){
        return traderRepository.findById(traderId).get();
    }

    public List<Trader> findAll(){
        return traderRepository.findAll();
    }

    public void saveAll(List<Trader> traderList){
        traderRepository.saveAll(traderList);
    }

    public Trader save(Trader trader){
        return traderRepository.save(trader);
    }

    public List<Trader> getTraderListByCurrentHaveLotEqualsZero(String shareCode){
        Share share = shareRepository.findByCode(shareCode);
        return traderRepository.getTraderListByCurrentHaveLotEqualsZero(batchUtil.getTraderId(), share.getPrice());
    }

    public void deleteTrader(Trader trader){
        traderRepository.delete(trader);
    }

    public BigDecimal selectPrice(){
        return PriceStepContext.priceStepList.get(random.nextInt(PriceStepContext.priceStepList.size()));
    }

    public void sendShareOrder(ShareOrder shareOrder){
        shareOrderService.sendShareOrder(shareOrder);
    }

    @Transactional
    public void createShareOrder(Share share, Long traderId) throws InterruptedException {
        Trader trader = traderRepository.findById(traderId).get();

        ShareOrder shareOrder = new ShareOrder();
        shareOrder.setTrader(trader);
        shareOrder.setCreateTime(LocalDateTime.now());
        shareOrder.setShareOrderStatus(trader.getTraderBehavior());
        shareOrder.setShareOrderType(RandomData.shareOrderType().name());
        if (shareOrder.getShareOrderType().equals(ShareOrderType.LIMIT.name())){
            shareOrder.setPrice(shareOrder.getShareOrderStatus().equals(ShareOrderStatus.BUY.name()) ? trader.getPriceBuy() : trader.getPriceSell());
        }
        shareOrder.set
    }
}
