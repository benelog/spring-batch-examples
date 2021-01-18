package kr.co.wikibook.batch.logbatch;

import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.repeat.RepeatStatus;

public class LogDiskSpaceTask implements Callable<RepeatStatus> {
  private final Logger log = LoggerFactory.getLogger(LogDiskSpaceTask.class);
  private final long usablePercentage;

  public LogDiskSpaceTask(long usablePercentage) {
    this.usablePercentage = usablePercentage;
  }

  @Override
  public RepeatStatus call() throws Exception {
    log.info("사용 가능한 디스크 용량 : {}%", this.usablePercentage);
    return RepeatStatus.FINISHED;
  }
}
