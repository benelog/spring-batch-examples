package kr.co.wikibook.batch.logbatch.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.JdbcJobInstanceDao;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.incrementer.H2SequenceMaxValueIncrementer;

class JdbcUpdateTaskletTest {

  @Test
  void executeDelete() {
    // given
    DataSource dataSource = this.buildDataSource();
    JdbcJobInstanceDao dao = this.buildJdbcJobInstanceDao(dataSource);
    dao.createJobInstance("testJob", buildParams("id", "aa"));
    dao.createJobInstance("testJob", buildParams("id", "bb"));

    String sql = "DELETE FROM BATCH_JOB_INSTANCE WHERE JOB_NAME = :jobName";
    var tasklet = new JdbcUpdateTasklet(dataSource, sql);

    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
    stepExecution.getJobExecution().getExecutionContext().put("jobName", "testJob");
    var stepContribution = new StepContribution(stepExecution);
    var chunkContext = new ChunkContext(new StepContext(stepExecution));

    // when
    tasklet.execute(stepContribution, chunkContext);

    // then
    assertThat(stepContribution.getWriteCount()).isEqualTo(2);
  }

  private DataSource buildDataSource() {
    return new EmbeddedDatabaseBuilder()
        .setName("test-repo1")
        .setType(EmbeddedDatabaseType.H2)
        .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
        .build();
  }

  private JdbcJobInstanceDao buildJdbcJobInstanceDao(DataSource dataSource) {
    var dao = new JdbcJobInstanceDao();
    dao.setJdbcTemplate(new JdbcTemplate(dataSource));
    dao.setJobIncrementer(new H2SequenceMaxValueIncrementer(dataSource, "BATCH_JOB_SEQ"));
    return dao;
  }

  private JobParameters buildParams(String key, String value) {
    return new JobParametersBuilder().addString(key, value)
        .toJobParameters();
  }
}
