package kr.co.wikibook.batch.healthcheck;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.util.FileSystemUtils;

public class BackupDailyDirectoryTask implements Callable<RepeatStatus> {

  private Logger logger = LoggerFactory.getLogger(BackupDailyDirectoryTask.class);
  private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private final Resource source;
  private final Resource targetParent;

  public BackupDailyDirectoryTask(Resource source, Resource targerParent) {
    this.source = source;
    this.targetParent = targerParent;
  }

  @Override
  public RepeatStatus call() throws IOException {
    LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
    String yearMonthDay = DAY_FORMAT.format(now);
    String targetDirectoryName = source.getFilename() + "_" + yearMonthDay;
    File targetDirectory = new File(targetParent.getFile(), targetDirectoryName);
    targetDirectory.mkdir();

    FileSystemUtils.copyRecursively(source.getFile(), targetDirectory);
    logger.info("Backup completed from {} to {}", source, targetDirectory);
    return RepeatStatus.FINISHED;
  }
}

