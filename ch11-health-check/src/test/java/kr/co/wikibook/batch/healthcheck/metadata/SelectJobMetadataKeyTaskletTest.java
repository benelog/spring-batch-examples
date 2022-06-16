package kr.co.wikibook.batch.healthcheck.metadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;

@ExtendWith(MockitoExtension.class)
public class SelectJobMetadataKeyTaskletTest {

  @Mock
  JobMetadataDao dao;

  @InjectMocks
  SelectJobMetadataKeyTasklet tasklet;

  @Test
  void executeWhenMetadataIsEmpty() {
    // given
    given(this.dao.selectMaxJobExecutionIdBefore(any(Instant.class))).willReturn(null);

    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("daysOfKeeping", 100L)
        .toJobParameters();
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
    var stepContribution = new StepContribution(stepExecution);
    var chunkContext = new ChunkContext(new StepContext(stepExecution));

    // when
    this.tasklet.execute(stepContribution, chunkContext);
    ExitStatus exitStatus = this.tasklet.afterStep(stepExecution);

    assertThat(exitStatus.getExitCode()).isEqualTo("EMPTY");
  }

  @Test
  void executeWhenMetadataIsSelected() {
    // given
    given(this.dao.selectMaxJobInstanceId(200L)).willReturn(100L);
    given(this.dao.selectMaxJobExecutionIdBefore(any(Instant.class))).willReturn(200L);
    given(this.dao.selectMaxStepExecutionId(200L)).willReturn(300L);

    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("daysOfKeeping", 30L)
        .toJobParameters();
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
    var stepContribution = new StepContribution(stepExecution);
    var chunkContext = new ChunkContext(new StepContext(stepExecution));

    // when
    this.tasklet.execute(stepContribution, chunkContext);
    ExitStatus exitStatus = this.tasklet.afterStep(stepExecution);

    // then
    assertThat(exitStatus).isEqualTo(ExitStatus.COMPLETED);
    ExecutionContext jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
    assertThat(jobExecutionContext.getLong("maxJobExecutionId")).isEqualTo(200L);
    assertThat(jobExecutionContext.getLong("maxJobInstanceId")).isEqualTo(100L);
    assertThat(jobExecutionContext.getLong("maxStepExecutionId")).isEqualTo(300L);
  }


}
