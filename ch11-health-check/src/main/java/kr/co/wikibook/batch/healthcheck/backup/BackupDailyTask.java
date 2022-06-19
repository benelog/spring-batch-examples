package kr.co.wikibook.batch.healthcheck.backup;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.FileSystemUtils;

public class BackupDailyTask implements Callable<RepeatStatus> {

  private Logger logger = LoggerFactory.getLogger(BackupDailyTask.class);
  private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private final File sourceDirectory;
  private final File targetParentDirectory;
  private final Clock clock;

  public BackupDailyTask(File sourceDirectory, File targetParentDictory, Clock clock) {
    this.sourceDirectory = sourceDirectory;
    this.targetParentDirectory = targetParentDictory;
    this.clock = clock;
  }

  @Override
  public RepeatStatus call() throws IOException {
    Instant now = this.clock.instant();
    LocalDate day = LocalDate.ofInstant(now, ZoneId.of("Asia/Seoul"));
    String yearMonthDay = DAY_FORMAT.format(day);
    String targetDirectoryName = this.sourceDirectory.getName() + "_" + yearMonthDay;
    File targetDirectory = new File(this.targetParentDirectory, targetDirectoryName);
    targetDirectory.mkdir();

    FileSystemUtils.copyRecursively(this.sourceDirectory, targetDirectory);
    logger.info("Backup completed from {} to {}", this.sourceDirectory, targetDirectory);
    return RepeatStatus.FINISHED;
  }
}
