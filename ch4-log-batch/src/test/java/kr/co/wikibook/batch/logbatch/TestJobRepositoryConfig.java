package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
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

@Configuration
public class TestJobRepositoryConfig {

  private final DataSource jobDb;

  public TestJobRepositoryConfig() {
    this.jobDb = new EmbeddedDatabaseBuilder()
        .setName("test-job-db")
        .setType(EmbeddedDatabaseType.H2)
        .addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
        .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
        .build();
  }

  @Component
  class TestBatchConfigurer extends DefaultBatchConfigurer {
    protected JobRepository createJobRepository() throws Exception {
      DataSource jobDb = TestJobRepositoryConfig.this.jobDb;
      JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
      factory.setDataSource(jobDb);
      factory.setTransactionManager(new DataSourceTransactionManager(jobDb));
      factory.afterPropertiesSet();
      return factory.getObject();
    }
  }
}
