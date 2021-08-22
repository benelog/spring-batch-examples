package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest("spring.batch.job.names=" + AccessLogJobConfig.JOB_NAME )
class AccessLogJobTest2 {
  @Test
  void launchJob(@Autowired Job job, @Autowired JobLauncher jobLauncher) throws Exception {
    JobParameters params = new JobParametersBuilder()
        .addString("accessLog", "file:./src/test/resources/sample-access-log.csv")
        .addLong("randomId", new Random().nextLong())
        .toJobParameters();
    JobExecution execution = jobLauncher.run(job, params);
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }
}
