package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest("spring.batch.job.names=" + AccessLogJobConfig.JOB_NAME )
class AccessLogJobTest {

  private JobLauncherTestUtils testUtils = new JobLauncherTestUtils();

  @BeforeEach
  void initJobLauncherTestUtils(
      @Autowired JobRepository jobRepository,
      @Autowired JobLauncher jobLauncher,
      @Autowired Job job) {
    this.testUtils.setJobRepository(jobRepository);
    this.testUtils.setJobLauncher(jobLauncher);
    this.testUtils.setJob(job);
  }

  @Test
  void launchJob() throws Exception {
    JobParameters params = this.testUtils.getUniqueJobParametersBuilder()
        .addString("accessLog", "file:./src/test/resources/sample-access-log.csv")
        .toJobParameters();
    JobExecution execution = this.testUtils.launchJob(params);
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }
}
