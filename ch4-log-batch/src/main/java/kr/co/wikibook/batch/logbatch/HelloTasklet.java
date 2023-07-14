package kr.co.wikibook.batch.logbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloTasklet implements Tasklet {

  private final Logger log = LoggerFactory.getLogger(HelloTasklet.class);

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    contribution.incrementReadCount();
    contribution.incrementWriteCount(1);
    log.info("Hello Batch : {}", chunkContext);
    return RepeatStatus.FINISHED;
  }
}
