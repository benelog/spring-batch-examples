package kr.co.wikibook.batch.healthcheck;

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

  private final Resource parentDirectory;
  private final int daysOfKeeping;
  private final Clock clock;

  public DeleteOldDirectoriesTask(Resource parentDirectory, int daysOfKeeping, Clock clock) {
    this.parentDirectory = parentDirectory;
    this.daysOfKeeping = daysOfKeeping;
    this.clock = clock;
  }
  @Override
  public RepeatStatus call() throws IOException {
    Instant now = this.clock.instant();
    Instant baseInstant = now.minus(daysOfKeeping, ChronoUnit.DAYS);
    long baseEpochMilli = baseInstant.toEpochMilli();
    File[] files = parentDirectory.getFile().listFiles();

    int deletedCount = 0;
    for(File file : files) {
      if (!file.isDirectory()) {
        continue;
      }
      if ( file.lastModified() > baseEpochMilli ) {
        boolean deleted = FileSystemUtils.deleteRecursively(file);
        if(deleted) {
          deletedCount++;
        }
      }
    }

    if(deletedCount == 0) {
      throw new RuntimeException("Deleted nothing");
    }
    return RepeatStatus.FINISHED;
  }
}
