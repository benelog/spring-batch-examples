package kr.co.wikibook.batch.healthcheck.metadata;

import javax.sql.DataSource;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class BatchTestConfig {
  @Bean
  public DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
        .generateUniqueName(true)
        .setType(EmbeddedDatabaseType.H2)
        .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
        .build();
  }

  @Bean
  public JobRepository jobRepository(DataSource dataSource) throws Exception {
    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
    factory.setDataSource(dataSource());
    factory.setTransactionManager(new DataSourceTransactionManager(dataSource));
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  @Bean
  public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
    jobLauncher.setJobRepository(jobRepository);
    jobLauncher.afterPropertiesSet();
    return jobLauncher;
  }
}
