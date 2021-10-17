package kr.co.wikibook.batch.healthcheck.retry;

import org.springframework.retry.annotation.CircuitBreaker;

public class FragileService extends UnstableService {
  public FragileService(int failures) {
    super(failures);
  }

  @CircuitBreaker(
      maxAttempts = 2,
      openTimeout = 200L,
      resetTimeout = 300L,
      include = RuntimeException.class
  )
  @Override
  public boolean call(String message) {
    return super.call(message);
  }
}
