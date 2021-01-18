package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest({
    "spring.batch.job.names=" + CheckStatusJobConfig.JOB_NAME,
    "spring.batch.job.enabled=false"
})
@SpringBatchTest
class CheckStatusJobTest {

  @Autowired
  JobLauncherTestUtils testUtils;

  @Test
  void launchCountAccessLogStep() throws Exception {
    JobExecution execution = testUtils.launchStep("countAccessLogStep");
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }

  @Test
  void launchLogDiskSpaceStep() throws Exception {
    var jobExecutionContext = new ExecutionContext();
    jobExecutionContext.putLong("usablePercentage", 50L);

    JobExecution execution = testUtils.launchStep("logDiskSpaceStep", jobExecutionContext);
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }
}
