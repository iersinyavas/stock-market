package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.service.TraderService;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteTraderTasklet implements Tasklet {

    private final BatchUtil batchUtil;
    private final TraderService traderService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws InterruptedException {
        Share share = batchUtil.getShare();
        List<Trader> traderList = traderService.getTraderListByCurrentHaveLotEqualsZero(share.getCode());
        traderList.forEach(trader -> traderService.deleteTrader(trader));
        return RepeatStatus.FINISHED;
    }


}
