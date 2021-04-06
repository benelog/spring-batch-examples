package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class BatchDbConfig {
  @Bean
  @ConfigurationProperties(prefix="main-db")
  public DataSource mainDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  @Primary
  @ConfigurationProperties(prefix="job-db")
  public DataSource jobDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  public BatchDataSourceInitializer dataSourceInitializer(
      BatchProperties props, ApplicationContext context) {
    return new BatchDataSourceInitializer(this.jobDataSource(), context, props);
  }

  @Bean
  public BatchConfigurer customBatchConfigurer() {
    return new MultiDataSourceBatchConfigurer(this.jobDataSource(), this.mainDataSource());
  }
}
