package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;

class CountAccessLogTaskTest {

  @Test
  void countAccessLog() {
    // given
    var dataSource = new TestDbConfig().dataSource();
    var task = new CountAccessLogTask(dataSource);

    var stepExecution = new StepExecution("testStep", new JobExecution(0L));
    var stepContext = new StepContext(stepExecution);
    var chunkContext = new ChunkContext(stepContext);
    var stepContribution = new StepContribution(stepExecution);

    // when
    RepeatStatus repeatStatus = task.execute(stepContribution, chunkContext);

    // then
    assertThat(repeatStatus).isEqualTo(RepeatStatus.FINISHED);
    long count = stepExecution.getExecutionContext().getLong("count");
    assertThat(count).isGreaterThanOrEqualTo(0L);
  }
}
