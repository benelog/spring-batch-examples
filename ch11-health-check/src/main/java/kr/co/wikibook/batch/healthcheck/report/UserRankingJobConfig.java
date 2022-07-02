package kr.co.wikibook.batch.healthcheck.report;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class UserRankingJobConfig {
  private JobBuilderFactory jobBuilderFactory;
  private StepBuilderFactory stepBuilderFactory;

  public UserRankingJobConfig(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }


  @Bean
  public Job userRankingJob() {
    return this.jobBuilderFactory.get("userRankingJob")
        .start(processAccessLogFlow())
        .split(new SimpleAsyncTaskExecutor())
        .add(analyzePurchasesFlow())
        .next(buildStep("사용자 순위 기록"))
        .end()
        .build();
  }

  @Bean
  public Flow processAccessLogFlow() {
    return new FlowBuilder<SimpleFlow>("접근 기록 처리")
        .start(buildStep("access log 전처리"))
        .next(buildStep("access 로그 분석"))
        .build();
  }

  @Bean
  public Flow analyzePurchasesFlow() {
    return new FlowBuilder<SimpleFlow>("구매 내역 분석")
        .start(buildStep("구매액 합계 계산"))
        .build();
  }

  private Step buildStep(String stepName) {
    return stepBuilderFactory.get(stepName)
        .tasklet(new LoggingTasklet(stepName + " 수행"))
        .build();
  }
}
