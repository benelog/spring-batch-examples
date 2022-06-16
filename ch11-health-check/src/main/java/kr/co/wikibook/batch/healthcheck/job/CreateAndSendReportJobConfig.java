package kr.co.wikibook.batch.healthcheck.job;

import kr.co.wikibook.batch.healthcheck.LoggingTasklet;
import kr.co.wikibook.batch.healthcheck.ReportFormat;
import kr.co.wikibook.batch.healthcheck.ReportFormatDecideTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateAndSendReportJobConfig {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  private final JobLauncher jobLauncher;

  public CreateAndSendReportJobConfig(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory,
      JobLauncher jobLauncher) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.jobLauncher = jobLauncher;
  }

  @Bean
  public Job createAndSendReportJob() {
    return this.jobBuilderFactory.get("createAndSendReportJob")
        .start(createReportJobStep(null))
        .next(sendReportJobStep(null))
        .build();
  }

  @Bean
  public Step createReportJobStep(Job createReportJob) {
    return this.stepBuilderFactory.get("createReportJobStep")
        .job(createReportJob)
        .launcher(jobLauncher)
        .parametersExtractor(new DefaultJobParametersExtractor())
        .build();
  }

  @Bean
  public Step sendReportJobStep(Job sendReportJob) {
    return this.stepBuilderFactory.get("sendReportJobStep")
        .job(sendReportJob)
        .launcher(jobLauncher)
        .parametersExtractor(new DefaultJobParametersExtractor())
        .build();
  }

  private JobParametersExtractor buildJobParameterExtator() {
    var extractor = new DefaultJobParametersExtractor();
    extractor.setKeys(new String[]{"reportDay", "random"});
    return extractor;
  }
}
