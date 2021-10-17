package kr.co.wikibook.batch.healthcheck.listener;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;

class StopWatchReporterTest {
  StopWatchReporter sut = new StopWatchReporter();

  @Test
  void report() {
    StepExecution stepExecution = new StepExecution("testStep", null);

    sut.beforeStep(stepExecution);

    sut.beforeProcess("item1");
    sut.afterProcess("item1", "");

    sut.beforeProcess("item2");
    sut.afterProcess("item2", "");

    sut.afterStep(stepExecution);
  }
}
