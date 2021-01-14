package kr.co.wikibook.batch.logbatch;

import java.time.LocalDate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = HelloParamJobConfig.JOB_NAME)
public class HelloParamJobConfig {

  public static final String JOB_NAME = "helloParamJob";
  public static final LocalDate INJECTED = null; // ApplicationContext 에 의해 주입되는 값을 표현

  @Bean
  public Job helloParamJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
    var noTransaction = new DefaultTransactionAttribute(Propagation.NOT_SUPPORTED.value());
    var incrementer = new RunIdIncrementer();
    incrementer.setKey("runId");

    var validator = new DefaultJobParametersValidator();
    validator.setRequiredKeys(new String[]{"runId"});

    return jobBuilderFactory.get(JOB_NAME)
        .validator(validator)
        .incrementer(incrementer)
        .start(stepBuilderFactory.get("helloDate")
            .tasklet(new HelloDateTask())
            .transactionAttribute(noTransaction)
            .build())
        .next(stepBuilderFactory.get("helloLocalDate")
            .tasklet(helloLocalDateTask(INJECTED))
            .transactionAttribute(noTransaction)
            .build())
        .build();
  }

  @Bean
  @JobScope
  public HelloLocalDateTask helloLocalDateTask(
      @Value("#{jobParameters[day]}")
      @DateTimeFormat(pattern = "yyyy.MM.dd")
      LocalDate helloDay) {
    return new HelloLocalDateTask(helloDay);
  }
}
