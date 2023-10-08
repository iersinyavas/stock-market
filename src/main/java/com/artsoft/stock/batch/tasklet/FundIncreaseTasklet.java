package com.artsoft.stock.batch.tasklet;

import com.artsoft.stock.entity.Share;
import com.artsoft.stock.service.share.Payable;
import com.artsoft.stock.util.BatchUtil;
import com.artsoft.stock.util.Factory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FundIncreaseTasklet implements Tasklet {

    private final Factory factory;
    private final BatchUtil batchUtil;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        Share share = batchUtil.getShare();
        if (Objects.nonNull(share.getFundIncrease().getValue())){
            Payable payable = factory.getPayable(share.getFundIncrease().getValue());
            payable.execute(share);
        }
        return RepeatStatus.FINISHED;
    }


}
