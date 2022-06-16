package kr.co.wikibook.batch.healthcheck.metadata;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class SelectJobMetadataKeyTasklet extends StepExecutionListenerSupport implements Tasklet {
  private final JobMetadataDao dao;

  public SelectJobMetadataKeyTasklet(JobMetadataDao dao) {
    this.dao = dao;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

    StepContext stepContext = chunkContext.getStepContext();
    Map<String, Object> jobParameters = stepContext.getJobParameters();
    long daysOfKeeping = (Long)jobParameters.get("daysOfKeeping");
    Instant baseDay = minusDaysAtSeoul(Instant.now(), daysOfKeeping);
    Long maxJobExecutionId = dao.selectMaxJobExecutionIdBefore(baseDay);

    if (maxJobExecutionId == null) {
      return RepeatStatus.FINISHED;
    }

    Long maxJobInstanceId = dao.selectMaxJobInstanceId(maxJobExecutionId);
    Long maxStepExecutionId = dao.selectMaxStepExecutionId(maxJobExecutionId);

    ExecutionContext jobExecutionContext = stepContext.getStepExecution().getJobExecution().getExecutionContext();
    jobExecutionContext.putLong("maxJobExecutionId", maxJobExecutionId);
    jobExecutionContext.putLong("maxJobInstanceId", maxJobInstanceId);
    jobExecutionContext.putLong("maxStepExecutionId", maxStepExecutionId);

    return RepeatStatus.FINISHED;
  }

  private Instant minusDaysAtSeoul(Instant instant, long days) {
    ZoneId seoul = ZoneId.of("Asia/Seoul");
    LocalDateTime seoulDateTime = LocalDateTime.ofInstant(instant, seoul);
    LocalDateTime dayBefore = seoulDateTime.minusDays(days);
    return dayBefore.toInstant(seoul.getRules().getOffset(dayBefore));
  }

  public ExitStatus afterStep(StepExecution stepExecution) {
    ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
    long maxJobExecutionId = jobExecutionContext.getLong("maxJobExecutionId", 0L);
    if (maxJobExecutionId == 0L) {
      return new ExitStatus("EMPTY");
    }
    return ExitStatus.COMPLETED;
  }
}