package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest({
    "spring.batch.job.names=" + HelloParamJobConfig.JOB_NAME,
    "spring.batch.job.enabled=false"
})
@SpringBatchTest
class HelloParamJobTest {

  @Test
  void launchJob(
      @Autowired JobExplorer jobExplorer,
      @Autowired JobLauncherTestUtils testUtils
  ) throws Exception {
    JobParameters params = new JobParametersBuilder(jobExplorer)
        .getNextJobParameters(testUtils.getJob())
        .addDate("helloDate", new Date(0L))
        .addString("day", "2020.11.01")
        .toJobParameters();

    JobExecution execution = testUtils.launchJob(params);
    assertThat(params.getLong("runId")).isNotNull();
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }
}
