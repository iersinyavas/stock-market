package com.artsoft.stock.batch.step;

import com.artsoft.stock.batch.tasklet.ShareOrderCreateTasklet;
import com.artsoft.stock.batch.tasklet.ShareOrderRollbackTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@RequiredArgsConstructor
@Component
public class ShareOrderRollbackStep {

    private final StepBuilderFactory stepBuilderFactory;
    private final ShareOrderRollbackTasklet shareOrderRollbackTasklet;

    public Step jobStep() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setTimeout(500000000);
        return stepBuilderFactory.get("ShareOrderRollbackStep")
                .tasklet(shareOrderRollbackTasklet)
                .allowStartIfComplete(true)
                .transactionAttribute(attribute)
                .build();
    }
}
