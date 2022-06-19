package kr.co.wikibook.batch.healthcheck.slow;

import static org.assertj.core.api.Assertions.assertThat;

import kr.co.wikibook.batch.healthcheck.TestBatchConfig;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {TestBatchConfig.class, SlowJobConfig.class})
@SpringBatchTest
class SlowJobTest {
  @Test
  void executeOnSaturday(@Autowired JobLauncherTestUtils testUtils) throws Exception {
    JobParameters params = testUtils.getUniqueJobParametersBuilder()
        .addLong("limit", 1L)
        .toJobParameters();

    JobExecution execution = testUtils.launchJob(params);
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }
}
