package kr.co.wikibook.batch.logbatch;

import org.springframework.batch.core.scope.JobScope;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class ScopeConfiguration {

  private static StepScope stepScope;

  private static JobScope jobScope;

  static {
    jobScope = new JobScope();
    jobScope.setAutoProxy(false);

    stepScope = new StepScope();
    stepScope.setAutoProxy(false);
  }

  @Bean
  public static StepScope stepScope() {
    return stepScope;
  }

  @Bean
  public static JobScope jobScope() {
    return jobScope;
  }
}