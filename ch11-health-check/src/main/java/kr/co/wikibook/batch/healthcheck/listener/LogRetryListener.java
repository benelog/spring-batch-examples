package kr.co.wikibook.batch.healthcheck.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

public class LogRetryListener implements RetryListener {
  private final Logger logger = LoggerFactory.getLogger(LogRetryListener.class);

  @Override
  public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
    logger.info("Retry open : {}", context);
    return true;
  }

  @Override
  public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
      Throwable throwable) {
    logger.warn("Retry 중 에러 {}", context, throwable);
  }

  @Override
  public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
      Throwable throwable) {
    logger.info("Retry close : {}", context);
  }
}
