package kr.co.wikibook.batch.healthcheck.listener;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.FileSystemResource;
import slf4jtest.LogMessage;
import slf4jtest.Settings;
import slf4jtest.TestLoggerFactory;

class LogResourceMetaTaskTest {

  @Test
  void execute(@TempDir Path tempPath) throws IOException {
    // given
    Path resourcePath = tempPath.resolve(Path.of("uri.txt"));
    Files.writeString(resourcePath, "");
    Instant lastModified = Instant.parse("2021-08-16T12:58:54.113Z");
    resourcePath.toFile().setLastModified(lastModified.toEpochMilli());
    var resource = new FileSystemResource(resourcePath);
    TestLoggerFactory loggerFactory = Settings.instance()
        .enableAll()
        .buildLogging();
    var listener = new LogResourceListener(resource, loggerFactory);

    // when
    listener.logLastModified();

    // then
    LogMessage logMessage = loggerFactory.lines().iterator().next();
    assertThat(logMessage.text).isEqualTo("uri.txt 파일 마지막 수정: 2021-08-16T12:58:54.113Z");
  }
}
