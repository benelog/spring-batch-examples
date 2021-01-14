package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(TestDbConfig.class)
class CountAccessLogTaskTest {

  @Test
  void countAccessLog(@Autowired DataSource dataSource) {
    // given
    var task = new CountAccessLogTask(dataSource);

    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
    var stepContribution = new StepContribution(stepExecution);
    var chunkContext = new ChunkContext(new StepContext(stepExecution));

    // when
    RepeatStatus repeatStatus = task.execute(stepContribution, chunkContext);

    // then
    assertThat(repeatStatus).isEqualTo(RepeatStatus.FINISHED);
    long count = stepExecution.getExecutionContext().getLong("count");
    assertThat(count).isGreaterThanOrEqualTo(0L);
  }
}
