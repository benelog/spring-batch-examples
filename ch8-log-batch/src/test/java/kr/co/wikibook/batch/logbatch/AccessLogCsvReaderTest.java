package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest({
    "spring.batch.job.names=" + AccessLogJobConfig.JOB_NAME,
    "spring.batch.job.enabled=false"
})
@SpringBatchTest
class AccessLogCsvReaderTest {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Test
  void readItems(@Autowired FlatFileItemReader<AccessLog> accessLogReader) throws Exception {
    // given
    JobParameters params = new JobParametersBuilder()
        .addString("accessLog", "classpath:/sample-access-log.csv")
        .toJobParameters();
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(params);

    StepScopeTestUtils.doInStepScope(stepExecution, () -> {
      // when
      accessLogReader.open(new ExecutionContext());
      int itemCount = 0;
      AccessLog item;
      while ((item = accessLogReader.read()) != null) {
        itemCount++;
        logger.debug("{}", item);
      }
      accessLogReader.close();

      // then
      assertThat(itemCount).isEqualTo(3);
      return null;
    });
  }
}
