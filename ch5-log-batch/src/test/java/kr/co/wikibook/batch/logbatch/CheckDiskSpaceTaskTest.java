package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;

class CheckDiskSpaceTaskTest {

  @Test
  void checkDiskSpace() {
    // given
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("directory", "/")
        .addLong("minUsablePercentage", 100L)
        .toJobParameters();
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
    var stepContribution = new StepContribution(stepExecution);
    var chunkContext = new ChunkContext(new StepContext(stepExecution));
    var task = new CheckDiskSpaceTask();

    // when, then
    assertThatThrownBy(() ->
        task.execute(stepContribution, chunkContext)
    ).isInstanceOf(IllegalStateException.class);

    ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
    long usablePercentage = jobExecutionContext.getLong("usablePercentage");
    assertThat(usablePercentage).isGreaterThan(0L);
  }
}
