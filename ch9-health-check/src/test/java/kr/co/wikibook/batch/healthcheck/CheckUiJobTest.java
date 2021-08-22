package kr.co.wikibook.batch.healthcheck;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {HealthCheckApplication.class, CheckUriJobConfig.class})
@SpringBatchTest
class CheckUriJobTest {
  @Test
  void execute(@Autowired JobLauncherTestUtils testUtils) throws Exception {
    JobParameters params = testUtils.getUniqueJobParametersBuilder()
        .addString(CheckUriJobConfig.INPUT_FILE_PARAM, "classpath:/ok-uris.txt")
        .toJobParameters();

    JobExecution execution = testUtils.launchJob(params);
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    Path outputFile = Paths.get(CheckUriJobConfig.OUTPUT_FILE_PATH);
    assertThat(outputFile).isNotEmptyFile();
  }
}
