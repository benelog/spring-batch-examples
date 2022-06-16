package kr.co.wikibook.batch.healthcheck;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;

class BackupDailyDirectoryTaskTest {

  private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Test
  void backupDailyDirectory(@TempDir Path tempPath) throws IOException {
    // given
    Path sourcePath = tempPath.resolve("source");
    sourcePath.toFile().mkdir();
    Path file = sourcePath.resolve(Path.of("test1.txt"));
    Files.write(file, List.of("test content"));

    Path backupParent = tempPath.resolve("backup");
    backupParent.toFile().mkdir();

    var task = new BackupDailyDirectoryTask(
        new FileSystemResource(sourcePath),
        new FileSystemResource(backupParent)
    );

    // when
    RepeatStatus status = task.call();

    // then
    assertThat(status).isEqualTo(RepeatStatus.FINISHED);

    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    String yearMonthDay = DAY_FORMAT.format(now);
    Path backupDirectory = backupParent.resolve("source_" + yearMonthDay);
    // 날짜가 고정되지 않음으로서 테스트에 날짜 형식 변환 코드가 들어감
    assertThat(Files.exists(backupDirectory)).isTrue();

    Path copiedFile = backupDirectory.resolve("test1.txt");
    assertThat(Files.exists(copiedFile)).isTrue();
  }
}
