package com.artsoft.stock.batch.step;

import com.artsoft.stock.batch.tasklet.TreaderCreateTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@RequiredArgsConstructor
@Component
public class TreaderCreateStep {

    private final StepBuilderFactory stepBuilderFactory;
    private final TreaderCreateTasklet treaderCreateTasklet;

    public Step jobStep() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setTimeout(500000000);
        return stepBuilderFactory.get("TreaderCreateStep")
                .tasklet(treaderCreateTasklet)
                .allowStartIfComplete(true)
                .transactionAttribute(attribute)
                .build();
    }
}
