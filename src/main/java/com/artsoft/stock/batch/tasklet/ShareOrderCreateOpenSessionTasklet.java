package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.service.ShareOrderService;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.service.TraderService;
import com.artsoft.stock.util.BatchUtil;
import com.artsoft.stock.util.GeneralEnumeration.*;
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
public class ShareOrderCreateOpenSessionTasklet implements Tasklet {

    private final ShareOrderService shareOrderService;
    private final ShareService shareService;
    private final TraderService traderService;
    private final BatchUtil batchUtil;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws InterruptedException {
        Thread.sleep(new Random().nextInt(5000));
        Share share = batchUtil.getShare();
        share = shareService.init(share);
        BlockingQueue<Long> traderQueue = traderService.getTraderList(share);
        while (traderQueue.size() != 0){
            shareOrderService.createShareOrderOpenSession(share, traderQueue.take().longValue());
        }

        return RepeatStatus.FINISHED;
    }


}
