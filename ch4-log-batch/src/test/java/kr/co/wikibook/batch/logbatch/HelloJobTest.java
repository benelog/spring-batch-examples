package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest("spring.batch.job.names=" + HelloJobConfig.JOB_NAME)
@ActiveProfiles("test")
@SpringBatchTest
class HelloJobTest {

  @Autowired
  JobLauncherTestUtils jobTester;

  @Test
  void launchJob() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
        .addJobParameters(jobTester.getUniqueJobParameters())
        .toJobParameters();

    JobExecution jobExec = jobTester.launchJob(jobParameters);

    ExitStatus exitStatus = jobExec.getExitStatus();
    assertThat(exitStatus).isEqualTo(ExitStatus.COMPLETED);
  }
}
