package com.artsoft.stock.base;

import com.artsoft.stock.context.StockMarketBatchContext;
import com.artsoft.stock.listener.StockMarketJobListener;
import com.artsoft.stock.util.BatchUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseBatchConfiguration {

    @Autowired
    protected BaseStepConfiguration baseStepConfiguration;
    @Autowired
    protected JobBuilderFactory jobBuilderFactory;

    @Autowired
    protected BatchUtil batchUtil;


    protected abstract Job createJob() throws IOException;

    protected SimpleJobBuilder createSimpleJobBuilder(String jobName, Step... steps){
        SimpleJobBuilder simpleJobBuilder = jobBuilderFactory.get(jobName)
                .start(steps[0]);
        Arrays.stream(steps).skip(1).forEach(simpleJobBuilder::next);
        return simpleJobBuilder;
    }

    protected SimpleJobBuilder createSimpleJobBuilder(String jobName, List<Step> steps){
        SimpleJobBuilder simpleJobBuilder = jobBuilderFactory.get(jobName)
                .start(steps.get(0));
        for (int i = 1; i < steps.size(); ++i) {
            if (simpleJobBuilder != null) {
                simpleJobBuilder.next(steps.get(i));
            }
        }
        return simpleJobBuilder;
    }

    protected abstract StockMarketBatchContext initContext();

    protected Step createStep(Tasklet tasklet){
        return baseStepConfiguration.createStep(tasklet);
    }

    protected Step createStep(Tasklet tasklet, StepExecutionListener listener){
        return baseStepConfiguration.createStep(tasklet, listener);
    }

    protected JobExecutionListener createExceptionListener() {
        return  new StockMarketJobListener(initContext());
    }
}
