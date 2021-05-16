package kr.co.wikibook.batch.logbatch.multidb;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchSupportConfig {

  private final BatchConfigurer batchConfigurer;

  public BatchSupportConfig(BatchConfigurer batchConfigurer) {
    this.batchConfigurer = batchConfigurer;
  }

  @Bean
  public JobRepository jobRepository() throws Exception {
    return this.batchConfigurer.getJobRepository();
  }

  @Bean
  public JobLauncher jobLauncher() throws Exception {
    return this.batchConfigurer.getJobLauncher();
  }

  @Bean
  public JobRegistry jobRegistry() {
    return new MapJobRegistry();
  }

  @Bean
  public JobExplorer jobExplorer() throws Exception {
    return this.batchConfigurer.getJobExplorer();
  }

  @Bean
  public JobBuilderFactory jobBuilders(JobRepository jobRepository) {
    return new JobBuilderFactory(jobRepository);
  }

  @Bean
  public StepBuilderFactory stepBuilders(JobRepository jobRepository) throws Exception {
    return new StepBuilderFactory(jobRepository, this.batchConfigurer.getTransactionManager());
  }
}
