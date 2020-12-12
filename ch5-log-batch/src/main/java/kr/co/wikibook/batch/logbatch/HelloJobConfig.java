package kr.co.wikibook.batch.logbatch;

import java.time.LocalDate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = HelloJobConfig.JOB_NAME)
public class HelloJobConfig {

  public static final String JOB_NAME = "helloJob";
  public static final LocalDate INJECTED = null; // ApplicationContext 에 의해 주입되는 값을 표현

  @Bean
  public Job helloJob(JobBuilderFactory jobFactory, StepBuilderFactory stepFactory) {
    var validator = new DefaultJobParametersValidator();
    validator.setRequiredKeys(new String[]{"runId"});

    var incrementer = new RunIdIncrementer();
    incrementer.setKey("runId");

    var noTransaction = new DefaultTransactionAttribute(Propagation.NOT_SUPPORTED.value());

    return jobFactory.get(JOB_NAME)
        .validator(validator)
        .incrementer(incrementer)
        .start(stepFactory.get("helloStep")
            .tasklet(new HelloTask())
            .transactionAttribute(noTransaction)
            .build())
        .next(stepFactory.get("helloDayStep")
            .tasklet(helloDayTask(INJECTED))
            .transactionAttribute(noTransaction)
            .build())
        .build();
  }

  @Bean
  @JobScope
  public HelloDayTask helloDayTask(
      @Value("#{jobParameters[helloDay]}")
      //@DateTimeFormat(pattern = "yyyy.MM.dd")
      LocalDate helloDay) {
    return new HelloDayTask(helloDay);
  }
}
