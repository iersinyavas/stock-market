package com.artsoft.stock.batch.step;

import com.artsoft.stock.batch.tasklet.ShareOrderMatcherTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@RequiredArgsConstructor
@Component
public class ShareOrderMatcherStep {

    private final StepBuilderFactory stepBuilderFactory;
    private final ShareOrderMatcherTasklet shareOrderMatcherTasklet;

    public Step jobStep() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setTimeout(500000000);
        return stepBuilderFactory.get("ShareOrderMatcherStep")
                .tasklet(shareOrderMatcherTasklet)
                .allowStartIfComplete(true)
                .transactionAttribute(attribute)
                .build();
    }
}
