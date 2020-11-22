package kr.co.wikibook.batch.logbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class RepeatTask implements Tasklet {

  private final Logger log = LoggerFactory.getLogger(RepeatTask.class);
  private int index = 0;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    log.info("index : {}", index);
    contribution.incrementWriteCount(1);
    index++;
    if (index < 3) {
      return RepeatStatus.CONTINUABLE;
    }
    return RepeatStatus.FINISHED;
  }
}

