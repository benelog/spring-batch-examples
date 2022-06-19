package kr.co.wikibook.batch.healthcheck.backup;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.repeat.RepeatStatus;

class BackupDailyTaskTest {

  @TempDir
  Path tempPath;

  @Test
  void backupDailyDirectory() throws IOException {
    // given
    Path sourcePath = prepareSourcePath("testDir", "test1.txt");
    Path targetParentPath = this.tempPath.resolve("backup");
    Instant executedAt = Instant.parse("2022-06-10T15:14:16Z");

    var task = new BackupDailyTask(
        sourcePath.toFile(),
        targetParentPath.toFile(),
        Clock.fixed(executedAt, ZoneOffset.UTC)
    );

    // when
    RepeatStatus status = task.call();

    // then
    assertThat(status).isEqualTo(RepeatStatus.FINISHED);

    Path backupDirectory = targetParentPath.resolve("testDir_2022-06-11");
    assertThat(Files.exists(backupDirectory)).isTrue();

    Path copiedFile = backupDirectory.resolve("test1.txt");
    assertThat(Files.exists(copiedFile)).isTrue();
  }

  private Path prepareSourcePath(String directoryName, String fileName) throws IOException {
    Path sourcePath = this.tempPath.resolve(directoryName);
    sourcePath.toFile().mkdir();
    Path file = sourcePath.resolve(fileName);
    Files.writeString(file, "test content");
    return sourcePath;
  }
}
