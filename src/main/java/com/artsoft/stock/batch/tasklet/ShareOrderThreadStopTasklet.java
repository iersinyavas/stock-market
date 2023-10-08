package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.batch.ShareOrderCreator;
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
public class ShareOrderThreadStopTasklet implements Tasklet {

    private final ShareOrderCreator shareOrderCreator;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws InterruptedException {
        shareOrderCreator.lock();
        log.info("ShareOrderCreator bekliyor...");
        return RepeatStatus.FINISHED;
    }


}
