package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest({
    "spring.batch.job.names=" + CollectBlogPostJobConfig.JOB_NAME,
    "spring.batch.job.enabled=false"
})
@SpringBatchTest
class CollectBlogPostJobTest {

  @Test
  void launchJob(@Autowired JobLauncherTestUtils testUtils) throws Exception {
    JobExecution jobExec = testUtils.launchJob(testUtils.getUniqueJobParameters());
    ExitStatus exitStatus = jobExec.getExitStatus();
    assertThat(exitStatus).isEqualTo(ExitStatus.COMPLETED);
  }
}
