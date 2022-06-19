package kr.co.wikibook.batch.healthcheck.slow;

import kr.co.wikibook.batch.healthcheck.support.Transactions;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlowJobConfig {
  private JobBuilderFactory jobBuilderFactory;
  private StepBuilderFactory stepBuilderFactory;

  public SlowJobConfig(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job slowJob() {
    return this.jobBuilderFactory.get("slowJob")
        .start(buildRepeatSleepStep())
        .build();
  }

  private Step buildRepeatSleepStep() {
    return this.stepBuilderFactory.get("repeatSleepStep")
        .tasklet(new RepeatSleepTasklet())
        .transactionAttribute(Transactions.TX_NOT_SUPPORTED)
        .build();
  }
}
