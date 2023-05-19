package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.batch.ShareOrderCreator;
import com.artsoft.stock.entity.Share;
import com.artsoft.stock.service.ShareOrderService;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.service.TraderService;
import com.artsoft.stock.util.GeneralEnumeration.ShareCode;
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
public class ShareOrderCreateTasklet implements Tasklet {

    private final ShareOrderCreator shareOrderCreator;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws InterruptedException {
        shareOrderCreator.start();
        return RepeatStatus.FINISHED;
    }


}
