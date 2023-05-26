package com.artsoft.stock.batch.step;

import com.artsoft.stock.batch.tasklet.DeleteTraderTasklet;
import com.artsoft.stock.batch.tasklet.UpdateSharePriceTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@RequiredArgsConstructor
@Component
public class DeleteTraderStep {

    private final StepBuilderFactory stepBuilderFactory;
    private final DeleteTraderTasklet deleteTraderTasklet;

    public Step jobStep() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setTimeout(500000000);
        return stepBuilderFactory.get("DeleteTraderStep")
                .tasklet(deleteTraderTasklet)
                .allowStartIfComplete(true)
                .transactionAttribute(attribute)
                .build();
    }
}
