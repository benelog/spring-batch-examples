package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = CheckStatusJobConfig.JOB_NAME)
public class CheckStatusJobConfig {

  public static final String JOB_NAME = "checkStatusJob";

  @Bean
  public Job checkStatusJob(
      JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
      DataSource dataSource
  ) {
    var validator = new DefaultJobParametersValidator();
    validator.setRequiredKeys(new String[]{"directory", "minUsablePercentage"}); // <1>

    var noTransaction = new DefaultTransactionAttribute(Propagation.NOT_SUPPORTED.value());

    return jobBuilderFactory.get(JOB_NAME)
        .validator(validator)
        .start(stepBuilderFactory.get("countAccessLogStep")
            .tasklet(new CountAccessLogTask(dataSource))
            .transactionAttribute(noTransaction)
            .build()
        ).next(stepBuilderFactory.get("checkDiskSpaceStep")
            .tasklet(new CheckDiskSpaceTask())
            .transactionAttribute(noTransaction)
            .build()
        ).next(stepBuilderFactory.get("logDiskSpaceStep")
            .tasklet(logDiskSpaceTask(0L))
            .transactionAttribute(noTransaction)
            .build()
        )
        .build();
  }

  @Bean
  @StepScope
  public Tasklet logDiskSpaceTask(
      @Value("#{jobExecutionContext['usablePercentage']}") long usablePercentage
  ) {

    var logDiskSpaceTask = new LogDiskSpaceTask(usablePercentage);
    var tasklet = new CallableTaskletAdapter();
    tasklet.setCallable(logDiskSpaceTask);
    return tasklet;
  }
}
