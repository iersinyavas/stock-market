package com.artsoft.stock.batch.step;

import com.artsoft.stock.batch.tasklet.ShareOrderCreateOpenSessionTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@RequiredArgsConstructor
@Component
public class ShareOrderCreateOpenSessionStep {

    private final StepBuilderFactory stepBuilderFactory;
    private final ShareOrderCreateOpenSessionTasklet shareOrderCreateOpenSessionTasklet;

    public Step jobStep() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setTimeout(500000000);
        return stepBuilderFactory.get("ShareOrderCreateOpenSessionStep")
                .tasklet(shareOrderCreateOpenSessionTasklet)
                .allowStartIfComplete(true)
                .transactionAttribute(attribute)
                .build();
    }
}
