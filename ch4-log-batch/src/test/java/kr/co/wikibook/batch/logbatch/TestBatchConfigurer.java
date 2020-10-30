package kr.co.wikibook.batch.logbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
public class TestBatchConfigurer implements BatchConfigurer, InitializingBean {
  private final Logger log = LoggerFactory.getLogger(TestBatchConfigurer.class);

  private final PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();
  private final SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
  private JobRepository jobRepository;
  private JobExplorer jobExplorer;

  @Override
  public void afterPropertiesSet() {
    try {
      var jobRepositoryFactory = new MapJobRepositoryFactoryBean(this.transactionManager);
      jobRepositoryFactory.afterPropertiesSet();
      this.jobRepository = jobRepositoryFactory.getObject();

      var jobExplorerFactory = new MapJobExplorerFactoryBean(jobRepositoryFactory);
      jobExplorerFactory.afterPropertiesSet();
      this.jobExplorer = jobExplorerFactory.getObject();

      this.jobLauncher.setJobRepository(jobRepository);
      this.jobLauncher.afterPropertiesSet();
    } catch (Exception e) {
      throw new BatchConfigurationException(e);
    }
    log.info("TestBatchConfigurer initialized");
  }

  @Override
  public PlatformTransactionManager getTransactionManager() {
    return this.transactionManager;
  }

  @Override
  public JobLauncher getJobLauncher() {
    return this.jobLauncher;
  }

  @Override
  public JobRepository getJobRepository() {
    return this.jobRepository;
  }

  @Override
  public JobExplorer getJobExplorer() {
    return this.jobExplorer;
  }
}
