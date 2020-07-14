package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AccessLogAnalysisJobConfig {
  @Bean
  public CommandLineRunner accessLogCsvToDbTask(
      @Value("${access-log}") Resource resource,
      DataSource dataSource) {
    var reader = new AccessLogCsvReader(resource);
    var writer = new AccessLogDbWriter(dataSource);
    return new AccessLogCsvToDbTask(reader, writer, 300);
  }
}
