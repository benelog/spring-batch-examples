package kr.co.wikibook.batch.logbatch.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.SecureRandom;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(BatchTestConfig.class)
class RemoveOldJobMetadataJobTest {

  @Test
  void launch(
      @Autowired DataSource dataSource,
      @Autowired JobRepository jobRepository,
      @Autowired JobLauncher jobLauncher
  ) throws Exception {

    // given
    var jobFactory = new RemoveOldJobMetadataJobFactory(jobRepository, dataSource, "BATCH_");
    Job job = jobFactory.createJob("removeJobOldMetadataJob");

    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("daysOfKeeping", 10L)
        .addLong("randomId", new SecureRandom().nextLong())
        .toJobParameters();

    // when
    JobExecution jobExecution = jobLauncher.run(job, jobParameters);

    // then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
  }
}
