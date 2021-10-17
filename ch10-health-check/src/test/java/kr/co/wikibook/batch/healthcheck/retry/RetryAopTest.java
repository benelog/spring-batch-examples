package kr.co.wikibook.batch.healthcheck.retry;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import kr.co.wikibook.batch.healthcheck.retry.RetryAopTest.RetryConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(RetryConfig.class)
class RetryAopTest {

  @Configuration
  @EnableRetry
  public static class RetryConfig {

    @Bean
    public UnstableService fail3Service() {
      return new UnstableService(3);
    }

    @Bean
    public UnstableService fail4Service() {
      return new UnstableService(4);
    }

    @Bean
    public FragileService fail2Service() {
      return new FragileService(2);
    }
  }

  @Test
  void retry(@Autowired UnstableService fail3Service) {
    boolean success = fail3Service.call("Hello.");
    assertThat(fail3Service.getTryCount()).isEqualTo(4);
    assertThat(success).isTrue();
  }

  @Test
  void retryAndRecover(@Autowired UnstableService fail4Service) {
    boolean success = fail4Service.call("Hello.");
    assertThat(fail4Service.getTryCount()).isEqualTo(4);
    assertThat(success).isFalse();
  }

  @Test
  void recoveredWithCircuit(@Autowired FragileService fail2Service) throws InterruptedException {
    executeAndAssert(fail2Service, 1, false);
    executeAndAssert(fail2Service, 2, false);
    executeAndAssert(fail2Service, 2, false); // 차단기가 열려서 누적 시도횟수가 증가하지 않음
    TimeUnit.MILLISECONDS.sleep(310);

    // 차단기가 닫힘
    executeAndAssert(fail2Service, 3, true);
  }

  private void executeAndAssert(FragileService service, int tryCount, boolean expectedSuccess) {
    boolean success = service.call("Hello!");
    assertThat(service.getTryCount()).isEqualTo(tryCount);
    assertThat(success).isEqualTo(expectedSuccess);
  }
}
