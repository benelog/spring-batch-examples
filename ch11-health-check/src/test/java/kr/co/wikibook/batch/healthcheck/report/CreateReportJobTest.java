package kr.co.wikibook.batch.healthcheck.report;

import static org.assertj.core.api.Assertions.assertThat;

import kr.co.wikibook.batch.healthcheck.TestBatchConfig;
import kr.co.wikibook.batch.healthcheck.report.CreateReportJobConfig;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {TestBatchConfig.class, CreateReportJobConfig.class})
@SpringBatchTest
class CreateReportJobTest {
  @Autowired JobLauncherTestUtils testUtils;

  @Test
  void executeOnSaturday() throws Exception {
    JobParameters params = testUtils.getUniqueJobParametersBuilder()
        .addString("reportDay", "2022-06-11")
        .toJobParameters();

    JobExecution execution = testUtils.launchJob(params);
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }

  @Test
  void executeOnSunday() throws Exception {
    JobParameters params = testUtils.getUniqueJobParametersBuilder()
        .addString("reportDay", "2022-06-12")
        .toJobParameters();

    JobExecution execution = testUtils.launchJob(params);
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }

  @Test
  void executeOn1stDayOfMonth() throws Exception {
    JobParameters params = testUtils.getUniqueJobParametersBuilder()
        .addString("reportDay", "2022-07-01")
        .toJobParameters();

    JobExecution execution1 = testUtils.launchJob(params);
    assertThat(execution1.getExitStatus()).isEqualTo(ExitStatus.STOPPED);

    JobExecution execution2 = testUtils.launchJob(params);
    assertThat(execution2.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }
}
