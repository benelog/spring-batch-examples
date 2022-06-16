package kr.co.wikibook.batch.healthcheck.job;

import static org.assertj.core.api.Assertions.assertThat;

import kr.co.wikibook.batch.healthcheck.support.JobTestSupports;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CreateReportByDeciderJobTest {

  JobLauncherTestUtils testUtils;

  @BeforeEach
  void initTestUtils(
      @Autowired JobRepository jobRepository,
      @Autowired Job createReportByDeciderJob
  ) {
    this.testUtils = JobTestSupports.getJobLauncherTestUtils(
        createReportByDeciderJob,
        jobRepository
    );
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
