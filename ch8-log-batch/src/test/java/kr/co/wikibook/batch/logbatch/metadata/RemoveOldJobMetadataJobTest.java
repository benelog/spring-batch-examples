package kr.co.wikibook.batch.logbatch.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest("spring.batch.job.names=" + RemoveOldJobMetadataJobConfig.JOB_NAME )
class RemoveOldJobMetadataJobTest {

  private JobLauncherTestUtils testUtils = new JobLauncherTestUtils();

  @BeforeEach
  void initJobLauncherTestUtils(
      @Autowired JobRepository jobRepository,
      @Autowired JobLauncher jobLauncher,
      @Autowired @Qualifier("removeOldJobMetadataJob") Job job) {
    this.testUtils.setJobRepository(jobRepository);
    this.testUtils.setJobLauncher(jobLauncher);
    this.testUtils.setJob(job);
  }

  @Test
  void launch() throws Exception {
    JobParameters jobParameters = testUtils.getUniqueJobParametersBuilder()
        .addLong("daysOfKeeping", 10L)
        .toJobParameters();
    JobExecution jobExecution = testUtils.launchJob(jobParameters);
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
  }
}
