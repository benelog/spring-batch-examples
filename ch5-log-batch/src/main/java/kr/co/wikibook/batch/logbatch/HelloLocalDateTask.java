package kr.co.wikibook.batch.logbatch;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloLocalDateTask implements Tasklet {
  private final Logger log = LoggerFactory.getLogger(HelloLocalDateTask.class);
  private final LocalDate day;

  public HelloLocalDateTask(LocalDate day) {
    this.day = day;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    log.info("The day is {} ", day);
    return RepeatStatus.FINISHED;
  }
}
