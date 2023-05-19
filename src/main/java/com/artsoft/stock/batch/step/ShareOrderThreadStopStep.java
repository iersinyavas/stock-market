package com.artsoft.stock.batch.step;

import com.artsoft.stock.batch.tasklet.ShareOrderThreadStopTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@RequiredArgsConstructor
@Component
public class ShareOrderThreadStopStep {

    private final StepBuilderFactory stepBuilderFactory;
    private final ShareOrderThreadStopTasklet shareOrderThreadStopTasklet;

    public Step jobStep() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setTimeout(500000000);
        return stepBuilderFactory.get("ShareOrderThreadStopStep")
                .tasklet(shareOrderThreadStopTasklet)
                .allowStartIfComplete(true)
                .transactionAttribute(attribute)
                .build();
    }
}
