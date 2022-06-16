package kr.co.wikibook.batch.healthcheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

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

  @Test
  void notDeleteOldFile() throws IOException {
    // given
    Path file = this.tempPath.resolve(Path.of("test1.txt"));
    Files.write(file, List.of("test content"));
    Instant lastModifiedTime = Instant.parse("2022-01-11T11:14:16Z");
    Files.setLastModifiedTime(file, FileTime.from(lastModifiedTime));

    DeleteOldDirectoriesTask task = createTask("2022-01-16T11:14:16Z", 4);

    // when, then
    assertThatThrownBy(() -> task.call()).isInstanceOf(RuntimeException.class);
  }

  private Path createDirAndFile(String directoryName, String lastModifiedAt) throws IOException {
    Path directoryPath = this.tempPath.resolve(directoryName);
    directoryPath.toFile().mkdir();
    Path file = directoryPath.resolve(Path.of("test1.txt"));
    Files.write(file, List.of("test content"));

    Instant lastModifiedTime = Instant.parse(lastModifiedAt);
    Files.setLastModifiedTime(directoryPath, FileTime.from(lastModifiedTime));
    return directoryPath;
  }

  private DeleteOldDirectoriesTask createTask(String executedAt, int daysOfKeeping) {
    Resource parent = new FileSystemResource(this.tempPath.toFile());
    Instant executionInstant = Instant.parse(executedAt);
    Clock clock = Clock.fixed(executionInstant, ZoneId.systemDefault());
    return new DeleteOldDirectoriesTask(parent, daysOfKeeping, clock);
  }
}
