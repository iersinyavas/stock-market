package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.batch.ShareOrderCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShareOrderCreateTasklet implements Tasklet {

    private final ShareOrderCreator shareOrderCreator;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws InterruptedException {
        if (ShareOrderCreator.firstWork){
            ShareOrderCreator.firstWork = false;
            shareOrderCreator.start();
        }else {
            ShareOrderCreator.passive = false;
            shareOrderCreator.openLock();
        }

        return RepeatStatus.FINISHED;
    }


}
