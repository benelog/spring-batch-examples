package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;

public class MethodInvokingTest {
  StepContribution stepContribution = null;
  ChunkContext chunkContext = null;

  @BeforeEach
  void setUp() {
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
    stepContribution = new StepContribution(stepExecution);
    chunkContext = new ChunkContext(new StepContext(stepExecution));
  }

  @Test
  void execute() throws Exception {
    // given
    var service = new MyService();
    var taskletAdapter = new MethodInvokingTaskletAdapter();
    taskletAdapter.setTargetObject(service);
    taskletAdapter.setTargetMethod("setMessage");
    taskletAdapter.setArguments(new Object[]{"Hello"});
    taskletAdapter.afterPropertiesSet();

    // when
    RepeatStatus taskStatus = taskletAdapter.execute(stepContribution, chunkContext);

    // then
    assertThat(stepContribution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    assertThat(taskStatus).isEqualTo(RepeatStatus.FINISHED);
    assertThat(service.getMessage()).isEqualTo("Hello");
  }
}
