package kr.co.wikibook.batch.healthcheck.backup;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.repeat.RepeatStatus;

class DeleteOldDirectoriesTaskTest {

  @TempDir
  Path tempPath;

  @Test
  void deleteOldDirectories() throws IOException {
    // given
    Path dir1 = createDirAndFile("dir1", "2022-01-11T11:14:16Z");
    Path dir2 = createDirAndFile("dir2", "2022-01-12T11:14:16Z");
    Path dir3 = createDirAndFile("dir3", "2022-01-13T11:14:16Z");
    DeleteOldDirectoriesTask task = createTask("2022-01-16T11:14:16Z", 4);

    // when
    RepeatStatus status = task.call();

    // then
    assertThat(status).isEqualTo(RepeatStatus.FINISHED);
    assertThat(Files.exists(dir1)).isTrue();
    assertThat(Files.exists(dir2)).isTrue();
    assertThat(Files.exists(dir3)).isFalse();
  }

  private Path createDirAndFile(String directoryName, String lastModifiedAt) throws IOException {
    Path directoryPath = this.tempPath.resolve(directoryName);
    Files.createDirectories(directoryPath);
    Path file = directoryPath.resolve("test1.txt");
    Files.writeString(file, "test content");

    Instant lastModifiedTime = Instant.parse(lastModifiedAt);
    Files.setLastModifiedTime(directoryPath, FileTime.from(lastModifiedTime));
    return directoryPath;
  }

  private DeleteOldDirectoriesTask createTask(String executedAt, int daysOfKeeping) {
    Instant executionInstant = Instant.parse(executedAt);
    Clock clock = Clock.fixed(executionInstant, ZoneOffset.UTC);
    return new DeleteOldDirectoriesTask(this.tempPath.toFile(), daysOfKeeping, clock);
  }
}
