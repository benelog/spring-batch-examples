package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Component;

//@Configuration
public class TestJobRepositoryConfig {

  private final DataSource jobDb;

  public TestJobRepositoryConfig() {
    this.jobDb = new EmbeddedDatabaseBuilder()
        .setName("test-job-db")
        .setType(EmbeddedDatabaseType.H2)
        .addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
        .build();
  }

  @Bean
  public BatchDataSourceInitializer dataSourceInitializer(BatchProperties props, ApplicationContext context) {
    return new BatchDataSourceInitializer(this.jobDb, context, props);
  }

  @Component
  class TestBatchConfigurer extends DefaultBatchConfigurer {
    protected JobRepository createJobRepository() throws Exception {
      JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
      factory.setDataSource(jobDb);
      factory.setTransactionManager(new DataSourceTransactionManager(jobDb));
      factory.afterPropertiesSet();
      return factory.getObject();
    }

    @Override
    public JobExplorer createJobExplorer() throws Exception {
      JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
      factory.setDataSource(jobDb);
      factory.afterPropertiesSet();
      return factory.getObject();
    }
  }
}
