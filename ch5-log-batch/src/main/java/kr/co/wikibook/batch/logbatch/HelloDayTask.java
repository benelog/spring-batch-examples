package kr.co.wikibook.batch.logbatch;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloDayTask implements Tasklet {
  private final Logger log = LoggerFactory.getLogger(HelloDayTask.class);
  private final LocalDate helloDay;

  public HelloDayTask(LocalDate helloDay) {
    this.helloDay = helloDay;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    log.info("helloDay {} ", helloDay);
    return RepeatStatus.FINISHED;
  }
}
