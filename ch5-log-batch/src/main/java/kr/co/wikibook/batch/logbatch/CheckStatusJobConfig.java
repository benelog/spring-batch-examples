package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = CheckStatusJobConfig.JOB_NAME)
public class CheckStatusJobConfig {

  public static final String JOB_NAME = "checkStatusJob";
  private static final TransactionAttribute NO_TX =
      new DefaultTransactionAttribute(Propagation.NOT_SUPPORTED.value());

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  public CheckStatusJobConfig(JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job checkStatusJob(DataSource dataSource) {
    var validator = new DefaultJobParametersValidator();
    validator.setRequiredKeys(new String[]{"directory", "minUsablePercentage"}); // <1>

    return jobBuilderFactory.get(JOB_NAME)
        .validator(validator)
        .start(this.buildCountAccessLogStep(dataSource))
        .next(this.buildCheckDiskSpaceStep())
        .next(this.buildLogDiskSpaceTask())
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

  private TaskletStep buildCountAccessLogStep(DataSource dataSource) {
    return stepBuilderFactory.get("countAccessLogStep")
        .tasklet(new CountAccessLogTask(dataSource))
        .transactionAttribute(NO_TX)
        .build();
  }

  private TaskletStep buildCheckDiskSpaceStep() {
    return stepBuilderFactory.get("checkDiskSpaceStep")
        .tasklet(new CheckDiskSpaceTask())
        .transactionAttribute(NO_TX)
        .build();
  }


  private TaskletStep buildLogDiskSpaceTask() {
    return stepBuilderFactory.get("logDiskSpaceStep")
        .tasklet(logDiskSpaceTask(0L))
        .transactionAttribute(NO_TX)
        .build();
  }
}
