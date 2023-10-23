package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.constant.GeneralEnumeration.*;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.service.TraderService;
import com.artsoft.stock.service.share.ShareService;
import com.artsoft.stock.util.BatchUtil;
import com.artsoft.stock.util.RandomData;
import com.artsoft.stock.util.TraderBehavior;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateTraderStateTasklet implements Tasklet {

    private final ShareService shareService;
    private final TraderService traderService;
    private final BatchUtil batchUtil;

    private Random random = new Random();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws InterruptedException {
        Share share = shareService.getShare(ShareCode.ALPHA.name());
        List<Trader> allTraderList = traderService.findAll();
        BigDecimal targetPrice = traderService.updateTargetPrice(share);
        allTraderList.forEach(trader -> {
            if (trader.getPrinceRangeBig().compareTo(targetPrice) > 0){
                trader.setTraderBehavior(TraderBehavior.SELLER.name());
            }else if(trader.getPrinceRangeBig().compareTo(targetPrice) < 0){
                trader.setTraderBehavior(TraderBehavior.BUYER.name());
            }else {
                trader.setTraderBehavior(RandomData.traderBehavior().name());
            }
            trader.setPrinceRangeBig(RandomData.randomShareOrderPrice(targetPrice.divide(BigDecimal.valueOf(2), 2, RoundingMode.FLOOR), targetPrice));
            trader.setPrinceRangeSmall(RandomData.randomShareOrderPrice(BigDecimal.ZERO, targetPrice.divide(BigDecimal.valueOf(2), 2, RoundingMode.FLOOR)));
        });
        traderService.saveAll(allTraderList);
        return RepeatStatus.FINISHED;
    }


}
