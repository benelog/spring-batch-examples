package kr.co.wikibook.batch.healthcheck.retry;

import static org.assertj.core.api.Assertions.assertThat;

import kr.co.wikibook.batch.healthcheck.listener.LogRetryListener;
import org.junit.jupiter.api.Test;
import org.springframework.retry.support.RetryTemplate;

class RetryTemplateTest {

  @Test
  void retryWithListener() {
    // given
    var retryTemplate = RetryTemplate.builder()
        .maxAttempts(3)
        .withListener(new LogRetryListener())
        .build();
    var service = new UnstableService(2);

    // when
    boolean success = retryTemplate.execute(context -> service.call("hello"));

    // then
    assertThat(success).isTrue();
    assertThat(service.getTryCount()).isEqualTo(3);
  }
}
