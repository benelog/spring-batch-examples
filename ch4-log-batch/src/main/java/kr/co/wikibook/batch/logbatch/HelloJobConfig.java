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

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = HelloJobConfig.JOB_NAME)
public class HelloJobConfig {

  public static final String JOB_NAME = "helloJob";

  @Bean
  public Job helloJob(JobBuilderFactory jobFactory, StepBuilderFactory stepFactory) {
    var validator = new DefaultJobParametersValidator();
    validator.setRequiredKeys(new String[]{"runId"});

    var incrementer = new RunIdIncrementer();
    incrementer.setKey("runId");

    return jobFactory.get(JOB_NAME)
        .validator(validator)
        .incrementer(incrementer)
        .start(stepFactory.get("helloStep")
            .tasklet(new HelloTask())
            .build())
        .next(stepFactory.get("helloDayStep")
            .tasklet(helloDayTask(null))
            .build())
        .build();
  }

  @Bean
  @JobScope
  public HelloDayTask helloDayTask(
      @Value("#{jobParameters[helloDay]}") @DateTimeFormat(pattern = "yyyy.MM.dd") LocalDate helloDay) {
    return new HelloDayTask(helloDay);
  }
}
