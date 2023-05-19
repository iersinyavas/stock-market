package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.entity.Trader;
import com.artsoft.stock.repository.ShareRepository;
import com.artsoft.stock.repository.TraderRepository;
import com.artsoft.stock.service.TraderService;
import com.artsoft.stock.util.BatchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TreaderCreateTasklet implements Tasklet {

    private final TraderService traderService;
    private final ShareRepository shareRepository;
    private final TraderRepository traderRepository;
    private final BatchUtil batchUtil;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {

        List<Trader> traderList = new ArrayList<>();
        Share share = batchUtil.getShare();
        while (share.getLot().compareTo(BigDecimal.ZERO) > 0){
            traderList.add(traderService.createTrader(share));
        }
        traderRepository.saveAll(traderList);
        shareRepository.save(share);
        return RepeatStatus.FINISHED;
    }
}
