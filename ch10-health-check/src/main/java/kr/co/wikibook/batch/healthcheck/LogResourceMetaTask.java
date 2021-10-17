package kr.co.wikibook.batch.healthcheck;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.Callable;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;

public class LogResourceMetaTask implements Callable<RepeatStatus> {

  private final Logger logger;
  private final Resource resource;

  public LogResourceMetaTask(Resource resource, ILoggerFactory loggerFactory) {
    this.resource = resource;
    this.logger = loggerFactory.getLogger(LogResourceMetaTask.class.getName());
  }

  @Override
  public RepeatStatus call() throws IOException {
    File file = this.resource.getFile();
    Instant lastModified = Instant.ofEpochMilli(file.lastModified());
    logger.info("{} 파일 마지막 수정: {}", file.getName(), lastModified);
    return RepeatStatus.FINISHED;
  }
}
