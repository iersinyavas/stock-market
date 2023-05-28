package com.artsoft.stock.batch.tasklet;

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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddMoneyBalanceTasklet implements Tasklet {

    private final BatchUtil batchUtil;
    private final TraderService traderService;
    private Random random = new Random();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws InterruptedException {
        List<Trader> traderList = traderService.getTraderList();
        for (Trader trader : traderList) {
            BigDecimal returnInvestment = trader.getBalance().divide(BigDecimal.valueOf(10000), RoundingMode.FLOOR).multiply(BigDecimal.valueOf(random.nextInt(1000)+1));
            trader.setBalance(trader.getBalance().add(returnInvestment));
            traderService.save(trader);
        }
        return RepeatStatus.FINISHED;
    }


}
