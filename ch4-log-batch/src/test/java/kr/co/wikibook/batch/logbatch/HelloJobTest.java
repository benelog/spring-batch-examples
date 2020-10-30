package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest({
    "spring.batch.job.names=" + HelloJobConfig.JOB_NAME,
    "spring.batch.job.enabled=false"
})
@SpringBatchTest
class HelloJobTest {

  @Autowired
  JobLauncherTestUtils jobTester;

  @Test
  void launchJob() throws Exception {
    JobParameters params = new JobParametersBuilder()
        .addString("runId", "myId")
        .addJobParameters(jobTester.getUniqueJobParameters())
        .toJobParameters();

    JobExecution execution = jobTester.launchJob(params);
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }

  @Test
  void launchJobWithInvalidParameter() {
    JobParameters params = jobTester.getUniqueJobParameters();
    assertThatExceptionOfType(JobParametersInvalidException.class)
        .isThrownBy(() -> jobTester.launchJob(params))
        .withMessageContaining("do not contain required keys: [runId]");
  }
}
