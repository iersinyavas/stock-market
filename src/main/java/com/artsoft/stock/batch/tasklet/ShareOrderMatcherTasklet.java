package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.batch.ShareOrderCreator;
import com.artsoft.stock.batch.ShareOrderMatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShareOrderMatcherTasklet implements Tasklet {

    private final ShareOrderMatcher shareOrderMatcher;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws InterruptedException {
        shareOrderMatcher.start();
        return RepeatStatus.FINISHED;
    }


}
