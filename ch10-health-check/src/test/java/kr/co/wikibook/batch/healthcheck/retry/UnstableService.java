package kr.co.wikibook.batch.healthcheck.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

public class UnstableService {
  private final Logger logger = LoggerFactory.getLogger(UnstableService.class);
  private final int failures; // 의도적으로 실패할 횟수
  private int tryCount = 0;

  public UnstableService(int failures) {
    this.failures = failures;
  }

  @Retryable(
      maxAttempts = 4,
      backoff = @Backoff( delay= 200L, multiplier = 2d, maxDelay = 600L)
  )
  public boolean call(String message) {
    this.tryCount++;
    if (this.tryCount <= this.failures) {
      throw new RuntimeException("실패 : " + tryCount);
    }
    logger.info("성공 : " + this.tryCount + ", " + message);
    return true;
  }

  @Recover
  public boolean recover(Throwable error, String message) {
    logger.warn("Recover 실행 : {}", message, error);
    return false;
  }

  public int getTryCount() {
    return this.tryCount;
  }
}
