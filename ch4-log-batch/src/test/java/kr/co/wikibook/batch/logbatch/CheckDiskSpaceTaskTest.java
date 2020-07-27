package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;

class CheckDiskSpaceTaskTest {

  @Test
  void checkDiskSpace() {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("directory", "/")
        .addLong("minUsablePercentage", 100L)
        .toJobParameters();
    var stepExecution = new StepExecution("testStep", new JobExecution(0L, jobParameters));
    var stepContext = new StepContext(stepExecution);
    var chunkContext = new ChunkContext(stepContext);
    var stepContribution = new StepContribution(stepExecution);

    CheckDiskSpaceTask task = new CheckDiskSpaceTask();
    assertThatThrownBy(() ->
        task.execute(stepContribution, chunkContext)
    ).isInstanceOf(IllegalStateException.class);
  }
}
