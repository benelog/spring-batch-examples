package kr.co.wikibook.batch.healthcheck.slow;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class RepeatSleepTasklet implements Tasklet {

  private final Logger log = LoggerFactory.getLogger(RepeatSleepTasklet.class);
  private int count = 0;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws InterruptedException {
    StepExecution stepExecution = contribution.getStepExecution();
    long jobExecutionId = stepExecution.getJobExecutionId();
    count++;
    log.info("jobExecutionId = {}, repeat count : {}", jobExecutionId, count);
    TimeUnit.SECONDS.sleep(1);

    JobParameters jobParameters = stepExecution.getJobParameters();
    Long limit = jobParameters.getLong("limit", Long.valueOf(1L));
    return RepeatStatus.continueIf(count < limit);
  }
}
