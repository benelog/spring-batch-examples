package kr.co.wikibook.batch.healthcheck.report;

import java.time.LocalDate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;

@Configuration
public class SendReportJobConfig {
   private JobBuilderFactory jobBuilderFactory;
  private StepBuilderFactory stepBuilderFactory;

  public SendReportJobConfig(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job sendReportJob() {
    return this.jobBuilderFactory.get("sendReportJob")
        .start(checkHolidayStep())
        .on("FAILED")
        .to(buildStep("보고서 전송 없이 종료"))

        .from(checkHolidayStep())
        .on("*")
        .to(buildStep("보고서 전송"))
        .end()
        .build();
  }

  @Bean
  public Step checkHolidayStep() {
    return this.stepBuilderFactory.get("checkHolidayStep")
        .tasklet(helloLocalDateTask(null))
        .build();
  }

  @Bean
  @JobScope
  public Tasklet helloLocalDateTask(
      @Value("#{jobParameters['reportDay']}")
      @DateTimeFormat(pattern = "yyyy-MM-dd")
      LocalDate day) {

    var tasklet = new CallableTaskletAdapter();
    tasklet.setCallable(new HolidayCheckTask(day));
    return tasklet;
  }

  private Step buildStep(String stepName) {
    return this.stepBuilderFactory.get(stepName)
        .tasklet(new LoggingTasklet(stepName + " 수행"))
        .build();
  }
}
