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
    assertThat(success).isTrue();
    assertThat(fail3Service.getTryCount()).isEqualTo(4);
  }

  @Test
  void retryAndRecover(@Autowired UnstableService fail4Service) {
    boolean success = fail4Service.call("Hello.");
    assertThat(success).isFalse();
    assertThat(fail4Service.getTryCount()).isEqualTo(4);
  }

  @Test
  void recoveredWithCircuit(@Autowired FragileService fail2Service) throws InterruptedException {
    executeAndAssert(fail2Service, false, 1);
    // call() 실행 후 recover()가 실행
    executeAndAssert(fail2Service, false, 2);
    // 최대 시도횟수 2번이상으로 circuit이 열림

    executeAndAssert(fail2Service, false, 2);
    // recover 결과가 나오고 실제 call() 실행 횟수 tryCount는 증가하지 않음

    TimeUnit.MILLISECONDS.sleep(310);
    // 'resetTimeout' 이상의 시간이 지나서 circuit이 닫힘

    executeAndAssert(fail2Service, true, 3);
    // 실제 call() 메스드가 실행되고 tryCount가 증가함.
  }

  private void executeAndAssert(FragileService service, boolean expectedSuccess, int tryCount) {
    boolean success = service.call("Hello!");
    assertThat(success).isEqualTo(expectedSuccess);
    assertThat(service.getTryCount()).isEqualTo(tryCount);
  }
}
