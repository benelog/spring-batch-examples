package kr.co.wikibook.batch.logbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = HelloJobConfig.JOB_NAME)
public class HelloJobConfig {

  public static final String JOB_NAME = "helloJob";

  @Bean
  public Job helloJob(JobBuilderFactory jobFactory, StepBuilderFactory stepFactory) {
    var validator = new DefaultJobParametersValidator();
    validator.setRequiredKeys(new String[]{"runId"});

    return jobFactory.get(JOB_NAME)
        .validator(validator)
        .start(stepFactory.get("helloStep")
            .tasklet(new HelloTask())
            .build())
        .build();
  }
}
