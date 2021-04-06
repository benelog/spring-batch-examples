package kr.co.wikibook.batch.logbatch;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

public class MultiDataSourceBatchConfigurer implements BatchConfigurer {
  private final DataSource taskletDataSource;
  private final DataSource jobDataSource;
  private JobRepository jobRepository;
  private JobExplorer jobExplorer;
  private JobLauncher jobLauncher;
  private DataSourceTransactionManager transactionManager;

  public MultiDataSourceBatchConfigurer(DataSource jobDataSource, DataSource taskletDataSource) {
    this.jobDataSource = jobDataSource;
    this.taskletDataSource = taskletDataSource;
  }

  @PostConstruct
  public void init() throws Exception {
    this.jobRepository  = createJobRepository(this.jobDataSource);
    this.jobExplorer = createJobExplorer(this.jobDataSource);
    this.jobLauncher = createJobLauncher(this.jobRepository);
    this.transactionManager = new DataSourceTransactionManager(this.taskletDataSource);
  }

  @Override
  public JobRepository getJobRepository() throws Exception {
    return this.jobRepository;
  }

  @Override
  public PlatformTransactionManager getTransactionManager() throws Exception {
    return this.transactionManager;
  }

  @Override
  public JobLauncher getJobLauncher() throws Exception {
    return this.jobLauncher;
  }

  @Override
  public JobExplorer getJobExplorer() throws Exception {
    return this.jobExplorer;
  }

  private JobRepository createJobRepository(DataSource dataSource) throws Exception {
    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
    factory.setDataSource(dataSource);
    factory.setTransactionManager(new DataSourceTransactionManager(dataSource));
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  private JobLauncher createJobLauncher(JobRepository jobRepository) throws Exception {
    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
    jobLauncher.setJobRepository(jobRepository);
    jobLauncher.afterPropertiesSet();
    return jobLauncher;
  }

  private JobExplorer createJobExplorer(DataSource dataSource) throws Exception {
    JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
    jobExplorerFactoryBean.setDataSource(dataSource);
    jobExplorerFactoryBean.afterPropertiesSet();
    return jobExplorerFactoryBean.getObject();
  }
}
