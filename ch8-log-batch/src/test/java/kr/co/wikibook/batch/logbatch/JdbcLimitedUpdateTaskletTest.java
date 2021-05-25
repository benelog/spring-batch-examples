package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

@SpringJUnitConfig(TestDbConfig.class)
@Transactional
class JdbcLimitedUpdateTaskletTest {
  static final String INSERT = "INSERT INTO access_log (access_date_time, ip, username) VALUES ";

  @Test
  @Sql(statements = {
      INSERT + "('2020-06-10 11:14', '175.242.91.54', 'benelog')",
      INSERT + "('2020-06-10 11:14', '192.168.0.1', 'benelog')",
      INSERT + "('2020-06-10 11:14', '192.168.0.3', 'benelog')"
  })
  public void execute(@Autowired DataSource dataSource) throws Exception {
    // given
    var tasklet = new JdbcLimitedUpdateTasklet(
        dataSource,
        "DELETE FROM access_log WHERE username = 'benelog'",
        10
    );
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
    var stepContribution = new StepContribution(stepExecution);
    var chunkContext = new ChunkContext(new StepContext(stepExecution));

    // when
    tasklet.execute(stepContribution, chunkContext);

    // then
    assertThat(stepContribution.getWriteCount()).isEqualTo(3);
  }
}
