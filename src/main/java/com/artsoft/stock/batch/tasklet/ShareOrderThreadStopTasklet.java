package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.batch.ShareOrderCreator;
import com.artsoft.stock.batch.ShareOrderMatcher;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.service.ShareOrderService;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.service.TraderService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

@Component
@RequiredArgsConstructor
public class ShareOrderThreadStopTasklet implements Tasklet {

    private final ShareOrderCreator shareOrderCreator;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws InterruptedException {
        shareOrderCreator.lock();
        return RepeatStatus.FINISHED;
    }


}
