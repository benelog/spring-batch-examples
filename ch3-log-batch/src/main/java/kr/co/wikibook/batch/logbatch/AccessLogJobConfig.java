package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

// java -Djob=accessLogJob -Daccess-log=file:./src/test/resources/sample-access-log.csv -jar build/libs/ch3-log-batch-0.0.1-SNAPSHOT.jar
// ./gradlew bootRun -Djob=accessLogJob -Daccess-log=file:../access-log.csv
@Configuration
@ConditionalOnProperty(value = "job", havingValue = "accessLogJob")
@EnableTask
public class AccessLogJobConfig {

//  @Autowired
//  DataSource dataSource;

  @Bean
  @Order(1)
  public CommandLineRunner accessLogCsvToDbTask(@Value("${access-log}") Resource resource, DataSource dataSource) {
    var reader = new AccessLogCsvReader(resource);
    var writer = new AccessLogDbWriter(dataSource);
    return new AccessLogCsvToDbTask(reader, writer, 300);
  }

  @Bean
  @Order(2)
  public CommandLineRunner userAccessSummaryDbToCsvTask(DataSource dataSource) {
    var reader = new UserAccessSummaryDbReader(dataSource);
    var writer = new UserAccessSummaryCsvWriter(new FileSystemResource("user-access-summary.csv"));
    return new UserAccessSummaryDbToCsvTask(reader, writer, 300);
  }
}
