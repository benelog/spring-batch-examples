package kr.co.wikibook.batch.logbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = HelloJobConfig.JOB_NAME)
public class HelloJobConfig {

  public static final String JOB_NAME = "helloJob";


  @Bean
  public Job helloJob(JobBuilderFactory jobFactory, StepBuilderFactory stepFactory) {
    var noTransaction = new DefaultTransactionAttribute(Propagation.NOT_SUPPORTED.value());
    var dummyTxManager = new ResourcelessTransactionManager();

    return jobFactory.get(JOB_NAME)
        .start(stepFactory.get("helloStep")
            .tasklet(new HelloTasklet())
            .transactionManager(new ResourcelessTransactionManager())
            .transactionAttribute(noTransaction)
            .build())
        .next(stepFactory.get("repeatStep")
            .tasklet(new RepeatTask())
            .transactionManager(new ResourcelessTransactionManager())
            .build())
        .build();
  }
}
