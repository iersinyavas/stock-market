package com.artsoft.stock.batch.step;

import com.artsoft.stock.batch.tasklet.FundIncreaseTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@RequiredArgsConstructor
@Component
public class FundIncreaseStep {

    private final StepBuilderFactory stepBuilderFactory;
    private final FundIncreaseTasklet fundIncreaseTasklet;

    public Step jobStep() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setTimeout(500000000);
        return stepBuilderFactory.get("FundIncreaseStep")
                .tasklet(fundIncreaseTasklet)
                .allowStartIfComplete(true)
                .transactionAttribute(attribute)
                .build();
    }
}
