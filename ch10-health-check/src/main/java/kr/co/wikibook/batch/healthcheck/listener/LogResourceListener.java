package kr.co.wikibook.batch.healthcheck.listener;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.core.io.Resource;

public class LogResourceListener {

  private final Logger logger;
  private final Resource resource;

  public LogResourceListener(Resource resource, ILoggerFactory loggerFactory) {
    this.resource = resource;
    this.logger = loggerFactory.getLogger(LogResourceListener.class.getName());
  }

  @BeforeJob
  public void logLastModified() throws IOException {
    File file = this.resource.getFile();
    Instant lastModified = Instant.ofEpochMilli(file.lastModified());
    this.logger.info("{} 파일 마지막 수정: {}", file.getName(), lastModified);
  }
}
