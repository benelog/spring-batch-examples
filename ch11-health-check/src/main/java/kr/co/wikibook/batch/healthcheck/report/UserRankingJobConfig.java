package kr.co.wikibook.batch.healthcheck.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final Logger logger = LoggerFactory.getLogger(UserRankingJobConfig.class);

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
        .start(processLogFlow())
        .split(new SimpleAsyncTaskExecutor())
        .add(sumPurchaseAmount())
        .next(buildStep("사용자 순위 기록"))
        .end()
        .build();
  }

  @Bean
  public Flow processLogFlow() {
    return new FlowBuilder<SimpleFlow>("방문 기록 분석")
        .start(buildStep("access log 전처리"))
        .next(buildStep("access 로그 분석"))
        .build();
  }

  @Bean
  public Flow sumPurchaseAmount() {
    return new FlowBuilder<SimpleFlow>("구매액 분석")
        .start(buildStep("기간내 구매액 합계 계산"))
        .build();
  }

  private Step buildStep(String stepName) {
    return stepBuilderFactory.get(stepName)
        .tasklet(new LoggingTasklet(stepName + " 수행"))
        .build();
  }
}
