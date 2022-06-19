package kr.co.wikibook.batch.healthcheck.backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.FileSystemUtils;

public class DeleteOldDirectoriesTask implements Callable<RepeatStatus> {

  private final File parentDirectory;
  private final int daysOfKeeping;
  private final Clock clock;

  public DeleteOldDirectoriesTask(File parentDirectory, int daysOfKeeping, Clock clock) {
    this.parentDirectory = parentDirectory;
    this.daysOfKeeping = daysOfKeeping;
    this.clock = clock;
  }
  @Override
  public RepeatStatus call() {
    parentDirectory.mkdir();
    Instant now = this.clock.instant();
    Instant baseInstant = now.minus(daysOfKeeping, ChronoUnit.DAYS);
    long baseEpochMilli = baseInstant.toEpochMilli();
    File[] files = parentDirectory.listFiles();

    for(File file : files) {
      if (!file.isDirectory()) {
        continue;
      }
      if ( file.lastModified() > baseEpochMilli ) {
        FileSystemUtils.deleteRecursively(file);
      }
    }
    return RepeatStatus.FINISHED;
  }
}
