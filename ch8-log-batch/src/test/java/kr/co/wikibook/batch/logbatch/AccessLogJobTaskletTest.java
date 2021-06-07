package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest({
    "spring.batch.job.names=" + AccessLogJobConfig.JOB_NAME,
    "spring.batch.job.enabled=false"
})
@Transactional(transactionManager = "mainTransactionManager")
class AccessLogJobTaskletTest {
  Logger logger = LoggerFactory.getLogger(AccessLogJobTaskletTest.class);

  @Test
  void executeAccessLogCsvToDb(@Autowired AccessLogJobConfig config) throws Exception {
    // given
    TaskletStep step = config.buildCsvToDbStep();
    Tasklet tasklet = step.getTasklet();
    ItemStream stream = (ItemStream) ReflectionTestUtils.getField(step, "stream");
    JobParameters params = new JobParametersBuilder()
        .addString("accessLog", "file:./src/test/resources/sample-access-log.csv")
        .toJobParameters();

    // when
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(params);
    StepContribution stepContribution = StepScopeTestUtils.doInStepScope(stepExecution, () -> {
          var contribution = new StepContribution(stepExecution);
          var chunkContext = new ChunkContext(new StepContext(stepExecution));
          var exContext = stepExecution.getExecutionContext();

          stream.open(exContext);
          tasklet.execute(contribution, chunkContext);
          stream.close();
          return contribution;
        }
    );

    // then
    assertThat(stepContribution.getWriteCount()).isEqualTo(3);
  }

  @Test
  void executeUserAccessSummaryDbToCsv(@Autowired AccessLogJobConfig config) throws Exception {
    // given
    TaskletStep step = config.buildDbToCsvStep();
    Tasklet tasklet = step.getTasklet();
    ItemStream stream = (ItemStream) ReflectionTestUtils.getField(step, "stream");

    // when
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
    var stepContribution = new StepContribution(stepExecution);
    var chunkContext = new ChunkContext(new StepContext(stepExecution));
    var exContext = stepExecution.getExecutionContext();

    stream.open(exContext);
    while (tasklet.execute(stepContribution, chunkContext).isContinuable()) {
      logger.info("continue to execute Tasklet : {}" , stepContribution);
      // 전체 데이터가 하나의 TX로 처리되므로 데이터건이 많으면 바람직하지 못한 못한 방식
    }
    stream.close();

    // then
    assertThat(stepContribution.getWriteCount()).isGreaterThan(0);
  }
}
