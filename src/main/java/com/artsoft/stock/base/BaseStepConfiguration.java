package com.artsoft.stock.base;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * @author: Bahattin Kaya
 * @since: 3.08.2022
 */
@Component
@RequiredArgsConstructor
public class BaseStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;
    private static final String STEP_NAME_SUFFIX = "Step";
    private static final int DEFAULT_TIMEOUT = 200000000;

    public Step createStep(Tasklet tasklet, String stepName) {
        return stepBuilderFactory.get(stepName)
                .tasklet(tasklet)
                .transactionAttribute(createTransactionAttribute())
                .build();
    }

    public Step createStep(Tasklet tasklet) {
        return createStep(tasklet, createStepName(tasklet));
    }

    public Step createStep(Tasklet tasklet, StepExecutionListener listener) {
        return stepBuilderFactory.get(createStepName(tasklet))
                .tasklet(tasklet)
                .listener(listener)
                .transactionAttribute(createTransactionAttribute())
                .build();
    }

    private TransactionAttribute createTransactionAttribute() {
        DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
        attribute.setTimeout(DEFAULT_TIMEOUT);
        return attribute;
    }

    private String createStepName(Tasklet tasklet) {
        return tasklet.getClass().getSimpleName() + STEP_NAME_SUFFIX;
    }
}
