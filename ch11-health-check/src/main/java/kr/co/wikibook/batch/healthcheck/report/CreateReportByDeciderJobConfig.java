package kr.co.wikibook.batch.healthcheck.report;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateReportByDeciderJobConfig {
  private JobBuilderFactory jobBuilderFactory;
  private StepBuilderFactory stepBuilderFactory;

  public CreateReportByDeciderJobConfig(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job createReportByDeciderJob() {

    var formatDecider = new ReportFormatDecider();

    return this.jobBuilderFactory.get("createReportByDeciderJob")
        .start(buildStep("시작"))
        .next(formatDecider)
        .on(ReportFormat.DAILY.name())
        .to(buildStep("일간 보고서 생성"))

        .from(formatDecider)
        .on(ReportFormat.WEEKLY.name())
        .to(buildStep("주간 보고서 생성"))

        .from(formatDecider)
        .on(ReportFormat.MONTHLY.name())
        .stopAndRestart(buildStep("월간 보고서 생성"))

        .from(formatDecider)
        .on("*")
        .fail()
        .end()
        .build();
  }

  private Step buildReportFormatStep() {
    return this.stepBuilderFactory.get("reportFormatStep")
        .tasklet(new ReportFormatDecideTasklet())
        .build();
  }

  private Step buildStep(String stepName) {
    return this.stepBuilderFactory.get(stepName)
        .tasklet(new LoggingTasklet(stepName + " 수행"))
        .build();
  }
}
