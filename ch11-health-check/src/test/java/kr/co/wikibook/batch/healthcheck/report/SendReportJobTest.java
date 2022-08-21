package kr.co.wikibook.batch.healthcheck.report;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.wikibook.batch.healthcheck.TestBatchConfig;

@SpringBootTest(classes = {TestBatchConfig.class, SendReportJobConfig.class})
@SpringBatchTest
class SendReportJobTest {
  @Autowired JobLauncherTestUtils testUtils;

  @Test
  void executeOnSunday() throws Exception {
    JobParameters params = testUtils.getUniqueJobParametersBuilder()
        .addString("reportDay", "2022-06-12")
        .toJobParameters();

    JobExecution execution = testUtils.launchJob(params);
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }

  @Test
  void executeOnMonday() throws Exception {
    JobParameters params = testUtils.getUniqueJobParametersBuilder()
        .addString("reportDay", "2022-06-13")
        .toJobParameters();

    JobExecution execution = testUtils.launchJob(params);
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }
}
