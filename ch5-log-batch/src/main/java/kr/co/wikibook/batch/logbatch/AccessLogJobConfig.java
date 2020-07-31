package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

// 실행 방법
// ./gradlew bootRun -Djob=accessLogJob --args="accessLog=file:../access-log.csv"
@Configuration
@ConditionalOnProperty(name = "job", havingValue = AccessLogJobConfig.JOB_NAME)
public class AccessLogJobConfig {

  public static final String JOB_NAME = "accessLogJob";
  public static final Resource INJECTED_RESOURCED = null;

  @Bean
  public Job accessLogJob(JobBuilderFactory jobFactory, StepBuilderFactory stepFactory,
      DataSource dataSource) {

    AccessLogCsvReader csvReader = this.accessLogCsvReader(INJECTED_RESOURCED);
    // Spring에 의해 주입되기 때문에 아무 값이나 넘겨도 됨.

    Resource userAccessOutput = new FileSystemResource("user-access-summary.csv");

    return jobFactory
        .get(JOB_NAME)
        .start(stepFactory.get("accessLogCsvToDb")
            .<AccessLog, AccessLog>chunk(300)
            .reader(csvReader) // resource는 Spring에 의해 주입
            .processor(new AccessLogProcessor())
            .writer(new AccessLogDbWriter(dataSource))
            .build())
        .next(stepFactory.get("userAccessSummaryDbToCsv")
            .<UserAccessSummary, UserAccessSummary>chunk(300)
            .reader(new UserAccessSummaryDbReader(dataSource))
            .writer(new UserAccessSummaryCsvWriter(userAccessOutput))
            .build())
        .build();
  }

  @Bean
  @JobScope
  public AccessLogCsvReader accessLogCsvReader(
      @Value("#{jobParameters[accessLog]}") Resource resource) {
    return new AccessLogCsvReader(resource);
  }
}
