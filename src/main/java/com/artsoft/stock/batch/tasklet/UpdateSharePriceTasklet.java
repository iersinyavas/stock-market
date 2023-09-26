package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.util.BatchUtil;
import com.artsoft.stock.util.PriceStepUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateSharePriceTasklet implements Tasklet {

    private final BatchUtil batchUtil;
    private final ShareService shareService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws InterruptedException {
        Share share = batchUtil.getShare();
        BigDecimal maxPrice = share.getPrice().add(share.getPrice().divide(BigDecimal.TEN, RoundingMode.FLOOR));
        BigDecimal remainder = maxPrice.remainder(PriceStepUtil.priceControlForStep(maxPrice));
        share.setMaxPrice(maxPrice.subtract(remainder));

        BigDecimal minPrice = share.getPrice().subtract(share.getPrice().divide(BigDecimal.TEN, RoundingMode.FLOOR));
        remainder = minPrice.remainder(PriceStepUtil.priceControlForStep(minPrice));
        share.setMinPrice(minPrice.add(PriceStepUtil.priceControlForStep(minPrice).subtract(remainder)));
        shareService.save(share);
        log.info("Oturum sonlandı...");
        return RepeatStatus.FINISHED;
    }


}
