package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

// 실행 방법
// ./gradlew bootRun -Djob=accessLogAnalysisJob --args="accessLog=file:../access-log.csv"
@Configuration
@ConditionalOnProperty(name = "job", havingValue = AccessLogJobConfig.JOB_NAME)
public class AccessLogJobConfig {

  public static final String JOB_NAME = "accessLogJob";

  @Bean
  public Job accessLogAnalysisJob(JobBuilderFactory jobFactory, StepBuilderFactory stepFactory) {
    var parametersValidator = new DefaultJobParametersValidator();
    parametersValidator.setRequiredKeys(new String[]{"accessLog"});

    return jobFactory
        .get(JOB_NAME)
        .validator(parametersValidator)
        .incrementer(new RunIdIncrementer())
        .start(
            stepFactory.get("accessLogCsvToDb")
                .tasklet(accessLogCsvToDbTask(null, null)) // Resource, DataSource는 Spring에 의해 주입
                .build())
        .next(
            stepFactory.get("userAccessSummaryDbToCsv")
                .tasklet(userAccessSummaryDbToCsvTask(null))
                .build())
        .build();
  }

  @Bean
  @JobScope
  public Tasklet accessLogCsvToDbTask(
      @Value("#{jobParameters[accessLog]}") Resource resource,
      DataSource dataSource) {
    var reader = new AccessLogCsvReader(resource);
    var writer = new AccessLogDbWriter(dataSource);
    return new AccessLogCsvToDbTask(reader, writer);
  }

  @Bean
  public Tasklet userAccessSummaryDbToCsvTask(DataSource dataSource) {
    var reader = new UserAccessSummaryDbReader(dataSource);
    var writer = new UserAccessSummaryCsvWriter(new FileSystemResource("user-access-summary.csv"));
    return new UserAccessSummaryDbToCsvTask(reader, writer);
  }
}
