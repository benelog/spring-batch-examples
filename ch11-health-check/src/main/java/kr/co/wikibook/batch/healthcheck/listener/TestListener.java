package kr.co.wikibook.batch.healthcheck.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;

public class TestListener  {

  @BeforeStep
  public void beforeStep(StepExecution stepExecution) {
    System.out.println("1111");
  }

  @AfterStep
  public ExitStatus afterStep(StepExecution stepExecution) {
    System.out.println("2222");

    return null;
  }
}
