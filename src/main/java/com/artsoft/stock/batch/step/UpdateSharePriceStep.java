package com.artsoft.stock.batch.step;

import com.artsoft.stock.batch.tasklet.TreaderCreateTasklet;
import com.artsoft.stock.batch.tasklet.UpdateSharePriceTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@RequiredArgsConstructor
@Component
public class UpdateSharePriceStep {

    private final StepBuilderFactory stepBuilderFactory;
    private final UpdateSharePriceTasklet updateSharePriceTasklet;

    public Step jobStep() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setTimeout(500000000);
        return stepBuilderFactory.get("UpdateSharePriceStep")
                .tasklet(updateSharePriceTasklet)
                .allowStartIfComplete(true)
                .transactionAttribute(attribute)
                .build();
    }
}