package kr.co.wikibook.batch.healthcheck.retry;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

class RetryTemplateTest {

  @Test
  void retry() {
    // given
    var retryPolicy = new MaxAttemptsRetryPolicy(4);

    var backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(200L);
    backOffPolicy.setMultiplier(2d);
    backOffPolicy.setMaxInterval(600L);

    var retryTemplate = new RetryTemplate();
    retryTemplate.setRetryPolicy(retryPolicy);
    retryTemplate.setBackOffPolicy(backOffPolicy);

    var service = new UnstableService(3);
    RetryCallback<Boolean, RuntimeException> action = (RetryContext context) -> {
      System.out.println(context);
      return service.call("hello");
    };

    // when
    Boolean success = retryTemplate.execute(action);

    // then
    assertThat(success).isTrue();
    assertThat(service.getTryCount()).isEqualTo(4);
  }

  @Test
  void retryWithBuilder() {
    // given
    var retryTemplate = RetryTemplate.builder()
        .maxAttempts(4)
        .exponentialBackoff(200L, 2d, 600L)
        .build();
    var service = new UnstableService(3);

    // when
    boolean success = retryTemplate.execute(context -> service.call("hello"));

    // then
    assertThat(success).isTrue();
    assertThat(service.getTryCount()).isEqualTo(4);
  }

  @Test
  void retryWithRecover() {
    // given
    var retryTemplate = RetryTemplate.builder()
        .maxAttempts(4)
        .exponentialBackoff(200L, 2d, 600L)
        .build();

    var service = new UnstableService(4);
    RetryCallback<Boolean, RuntimeException> action = (context) -> service.call("hello");
    RecoveryCallback<Boolean> recovery = (context) -> false;

    // when
    boolean success = retryTemplate.execute(action, recovery);

    // then
    assertThat(success).isFalse();
    assertThat(service.getTryCount()).isEqualTo(4);
  }
}
