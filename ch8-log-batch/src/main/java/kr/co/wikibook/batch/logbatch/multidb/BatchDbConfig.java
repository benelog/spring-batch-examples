package kr.co.wikibook.batch.logbatch.multidb;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class BatchDbConfig {

  @Bean
  @ConfigurationProperties(prefix = "main-db")
  public DataSource mainDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  @BatchDataSource
  @ConfigurationProperties(prefix = "job-db")
  public DataSource jobDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  @Profile("dev")
  public InitializingBean mainDbInitializer() {
    return () -> {
      ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
      populator.setContinueOnError(true);
      populator.addScript(new ClassPathResource("schema.sql"));
      DatabasePopulatorUtils.execute(populator, mainDataSource());
    };
  }

  @Bean
  public BatchDataSourceInitializer dataSourceInitializer(
      BatchProperties props, ApplicationContext context) {
    return new BatchDataSourceInitializer(this.jobDataSource(), context, props);
  }

  @Bean
  public BatchConfigurer customBatchConfigurer() {
    var transactionManager = new DataSourceTransactionManager(mainDataSource());
    return new MultiDataSourceBatchConfigurer(this.jobDataSource(), transactionManager);
  }
}
