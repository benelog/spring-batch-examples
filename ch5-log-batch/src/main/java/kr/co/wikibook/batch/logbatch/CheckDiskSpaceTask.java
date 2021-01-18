package kr.co.wikibook.batch.logbatch;

import java.io.File;
import java.util.Map;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class CheckDiskSpaceTask implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
    String directory = (String) jobParameters.get("directory");
    long minUsablePercentage = (long) jobParameters.get("minUsablePercentage");

    File file = new File(directory);
    long actualUsablePercentage = file.getUsableSpace() * 100 / file.getTotalSpace();

    JobExecution jobExecution = contribution.getStepExecution().getJobExecution();
    ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
    jobExecutionContext.putLong("usablePercentage", actualUsablePercentage);

    if (actualUsablePercentage < minUsablePercentage) {
      throw new IllegalStateException("디스크 용량이 기대치보다 작습니다 : " + actualUsablePercentage + "% 사용 가능");
    }
    return RepeatStatus.FINISHED;
  }
}
