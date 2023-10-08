package com.artsoft.stock.service;

import com.artsoft.stock.entity.Investment;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.util.BatchUtil;
import com.artsoft.stock.util.PriceStep;
import com.artsoft.stock.util.PriceStepContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareRepository shareRepository;
    private Random random = new Random();

    @Value("${share.remaining-balance-day}")
    private Integer remainingBalanceDay;

    public void save(Share share){
        shareRepository.save(share);
    }

    public Share getShare(String code){
        return shareRepository.findByCode(code);
    }

    public Share init(Share share){
        Map<BigDecimal, BlockingQueue> priceMap = new HashMap<>();
        PriceStep priceStep = new PriceStep(share.getPrice(), share.getMinPrice(), share.getMaxPrice());
        priceStep.initUpPrice(priceMap);
        priceStep.initDownPrice(priceMap);
        List<BigDecimal> collect = PriceStepContext.priceStepList.stream().distinct().collect(Collectors.toList());
        Collections.sort(collect, Collections.reverseOrder());
        PriceStepContext.priceStepList = collect;
        share.setPriceStep(priceStep);
        return share;
    }

    @Transactional
    public void dailyAccounting(Share share){
        share = shareRepository.findByCode(share.getCode());
        List<Investment> investmentList = share.getInvestmentList();

        if (!investmentList.isEmpty()){
            investmentList.stream().forEach(investment -> {
                investment.setPastDayInvestment(investment.getPastDayInvestment().add(BigDecimal.ONE));
                BigDecimal divide = BigDecimal.valueOf(investment.getReturnInvestmentRatio()).divide(BigDecimal.valueOf(100), 2, RoundingMode.FLOOR).multiply(investment.getInvestmentAmount());
                investment.setReturnInvestment(divide.multiply(BigDecimal.valueOf(random.nextInt(investment.getReturnInvestmentRatio())).add(BigDecimal.ONE))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.FLOOR));

                investment.setExpenses(divide.multiply(BigDecimal.valueOf(random.nextInt(investment.getExpensesRatio())).add(BigDecimal.ONE))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.FLOOR));
            });
            BigDecimal totalReturnInvestment = investmentList.stream().map(Investment::getReturnInvestment).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalExpenses = investmentList.stream().map(Investment::getExpenses).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal difference = totalReturnInvestment.subtract(totalExpenses);
            share.setProfit(share.getProfit().add(difference));
        }

        share.setPriceIncomeRatio(share.getPrice().divide(share.getLastNetProfit().divide(share.getFund(), 2, RoundingMode.FLOOR), 2, RoundingMode.FLOOR));
        share.setRemainingBalanceDay(share.getRemainingBalanceDay()-1);

        if (share.getRemainingBalanceDay().compareTo(0) == 0){
            share.setLastNetProfit(share.getProfit());
            share.setPastProfit(share.getPastProfit().add(share.getLastNetProfit()));
            share.setProfit(BigDecimal.ZERO);
            share.setOwnResources(share.getPastProfit().add(share.getFund()));
            share.setRemainingBalanceDay(remainingBalanceDay);

            BigDecimal annualEstimatedProfit = share.getLastNetProfit().multiply(BigDecimal.valueOf(4));
            share.setPriceIncomeRatio(share.getPrice().divide(annualEstimatedProfit.divide(share.getFund(), 2, RoundingMode.FLOOR), 2, RoundingMode.FLOOR));
        }
        
        share.setMarketValue(share.getPrice().multiply(share.getCurrentLot()));
        share.setMarketBookRatio(share.getMarketValue().divide(share.getOwnResources(), 2, RoundingMode.FLOOR));


        shareRepository.save(share);
    }

}
