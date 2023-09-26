package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyAccountingTasklet implements Tasklet {

    private final ShareService shareService;
    private final BatchUtil batchUtil;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws InterruptedException {
        Share share = batchUtil.getShare();
        shareService.dailyAccounting(share);
        return RepeatStatus.FINISHED;
    }


}
