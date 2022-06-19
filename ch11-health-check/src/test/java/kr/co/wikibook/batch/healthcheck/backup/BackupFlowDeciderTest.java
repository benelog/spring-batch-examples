package kr.co.wikibook.batch.healthcheck.backup;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;

class BackupFlowDeciderTest {

  BackupFlowDecider decider = new BackupFlowDecider();
  ExecutionContext executionContext = new ExecutionContext();

  @ParameterizedTest
  @MethodSource("provideParamsForDecide")
  void decide(
      int executionCount, long sourceSize, long usableSpace,
      FlowExecutionStatus expectedStatus
  ) {
    JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();
    jobExecution.setExecutionContext(executionContext);
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(
        jobExecution, "step", 1L
    );

    executionContext.putInt("executionCount", executionCount);
    executionContext.putLong("sourceSize", sourceSize);
    executionContext.putLong("usableSpace", usableSpace);
    FlowExecutionStatus actualStatus = decider.decide(jobExecution, stepExecution);
    assertThat(actualStatus).isEqualTo(expectedStatus);
  }

  static Stream<Arguments> provideParamsForDecide() {
    return Stream.of(
        Arguments.of(1, 1000L, 1001L, FlowExecutionStatus.COMPLETED),
        Arguments.of(1, 3000L, 2000L, new FlowExecutionStatus("RETRY")),
        Arguments.of(2, 3000L, 2000L, FlowExecutionStatus.FAILED)
    );
  }
}