package kr.co.wikibook.batch.healthcheck.backup;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import kr.co.wikibook.batch.healthcheck.TestBatchConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {TestBatchConfig.class, BackupDailyJobConfig.class})
@SpringBatchTest
class BackupDailyJobTest {
  @TempDir
  Path tempPath;

  @Test
  void execute(@Autowired JobLauncherTestUtils testUtils) throws Exception {
    Path sourcePath = prepareSourcePath("testDir", "test1.txt");
    Path targetParentPath = this.tempPath.resolve("backup");
    Files.createDirectories(targetParentPath);

    JobParameters params = testUtils.getUniqueJobParametersBuilder()
        .addString("sourceDirectory", sourcePath.toString())
        .addString("targetParentDirectory", targetParentPath.toString())
        .toJobParameters();

    JobExecution execution = testUtils.launchJob(params);
    assertThat(execution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }

  private Path prepareSourcePath(String directoryName, String fileName) throws IOException {
    Path sourcePath = this.tempPath.resolve(directoryName);
    sourcePath.toFile().mkdir();
    Path file = sourcePath.resolve(fileName);
    Files.writeString(file, "test content");
    return sourcePath;
  }
}
