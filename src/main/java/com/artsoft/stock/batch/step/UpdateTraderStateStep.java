package com.artsoft.stock.batch.step;

import com.artsoft.stock.batch.tasklet.AddMoneyBalanceTasklet;
import com.artsoft.stock.batch.tasklet.UpdateTraderStateTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@RequiredArgsConstructor
@Component
public class UpdateTraderStateStep {

    private final StepBuilderFactory stepBuilderFactory;
    private final UpdateTraderStateTasklet updateTraderStateTasklet;

    public Step jobStep() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setTimeout(500000000);
        return stepBuilderFactory.get("UpdateTraderStateStep")
                .tasklet(updateTraderStateTasklet)
                .allowStartIfComplete(true)
                .transactionAttribute(attribute)
                .build();
    }
}
