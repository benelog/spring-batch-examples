package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Component;

@Component
public class TestBatchConfigurer extends DefaultBatchConfigurer {
  @Override
  protected JobRepository createJobRepository() throws Exception {
    DataSource jobDataSource = new EmbeddedDatabaseBuilder()
        .setName("test-job-db")
        .setType(EmbeddedDatabaseType.H2)
        .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
        .build();
    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
    factory.setDataSource(jobDataSource);
    factory.setTransactionManager(new DataSourceTransactionManager(jobDataSource));
    factory.afterPropertiesSet();
    return factory.getObject();
  }
}
