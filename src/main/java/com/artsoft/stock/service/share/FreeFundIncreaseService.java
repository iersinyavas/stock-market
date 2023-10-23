package com.artsoft.stock.service.share;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.service.FundIncreaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FreeFundIncreaseService implements Payable {

    private final TraderRepository traderRepository;
    private final ShareRepository shareRepository;
    private final FundIncreaseService fundIncreaseService;
    @Override
    public void execute(Share share) {
        if (share.getCurrentLot().compareTo(BigDecimal.ZERO) > 0 && share.getLot().compareTo(BigDecimal.ZERO) > 0){
            BigDecimal freeFundRatio = share.getLot().divide(share.getCurrentLot(), 2, RoundingMode.FLOOR);
            List<Trader> freeFundTraderList = traderRepository.getTraderListByCurrentHaveLot();
            if (share.getFund().multiply(freeFundRatio).compareTo(share.getPastProfit()) <= 0){
                freeFundTraderList.stream().forEach(trader -> {
                    trader.setCurrentHaveLot(trader.getCurrentHaveLot().add(trader.getCurrentHaveLot().multiply(freeFundRatio)));
                    trader.setHaveLot(trader.getCurrentHaveLot());
                    trader.setPrinceRangeBig(trader.getPrinceRangeBig().divide(freeFundRatio.add(BigDecimal.ONE), 2, RoundingMode.FLOOR));
                    trader.setPrinceRangeSmall(trader.getPrinceRangeSmall().divide(freeFundRatio.add(BigDecimal.ONE), 2, RoundingMode.FLOOR));
                });
                share.setPrice(share.getPrice().divide(freeFundRatio.add(BigDecimal.ONE), 2, RoundingMode.FLOOR));
                share.setMaxPrice(share.getPrice().add(share.getPrice().divide(BigDecimal.TEN, RoundingMode.FLOOR)));
                share.setMinPrice(share.getPrice().subtract(share.getPrice().divide(BigDecimal.TEN, RoundingMode.FLOOR)));
                share.setCurrentLot(share.getCurrentLot().add(share.getLot()));
                share.setPastProfit(share.getPastProfit().subtract(share.getFund().multiply(freeFundRatio)));
                share.setFund(share.getFund().add(share.getFund().multiply(freeFundRatio)));
                share.setLot(BigDecimal.ZERO);
                share.setFundIncrease(fundIncreaseService.getFundIncrease(Long.valueOf(3)));
                shareRepository.save(share);
            }
        }
    }
}
