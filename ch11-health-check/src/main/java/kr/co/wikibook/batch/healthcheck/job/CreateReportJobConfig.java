package kr.co.wikibook.batch.healthcheck.job;

import kr.co.wikibook.batch.healthcheck.LoggingTasklet;
import kr.co.wikibook.batch.healthcheck.ReportFormat;
import kr.co.wikibook.batch.healthcheck.ReportFormatDecideTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateReportJobConfig {
  private JobBuilderFactory jobBuilderFactory;
  private StepBuilderFactory stepBuilderFactory;

  public CreateReportJobConfig(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job createReportJob() {
    Step reportFormatDecideStep = buildReportFormatStep();
    return this.jobBuilderFactory.get("createReportJobConfig")
        .start(reportFormatDecideStep)

        .on(ReportFormat.DAILY.name())
        .to(buildStep("일간 보고서 생성"))

        .from(reportFormatDecideStep)
        .on(ReportFormat.WEEKLY.name())
        .to(buildStep("주간 보고서 생성"))

        .from(reportFormatDecideStep)
        .on(ReportFormat.MONTHLY.name())
        .stopAndRestart(buildStep("월간 보고서 생성"))

        .from(reportFormatDecideStep)
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
