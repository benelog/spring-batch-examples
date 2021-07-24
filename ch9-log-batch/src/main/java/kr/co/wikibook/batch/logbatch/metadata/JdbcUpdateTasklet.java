package kr.co.wikibook.batch.logbatch.metadata;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcUpdateTasklet implements Tasklet {
  private final NamedParameterJdbcTemplate jdbcOperations;
  private final String sql;

  public JdbcUpdateTasklet(DataSource dataSource, String sql) {
    this.jdbcOperations = new NamedParameterJdbcTemplate(dataSource);
    this.sql = sql;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    Map<String, Object> jobExecutionContext = chunkContext.getStepContext().getJobExecutionContext();
    int affected = jdbcOperations.update(sql, jobExecutionContext);
    contribution.incrementWriteCount(affected);
    return RepeatStatus.FINISHED;
  }
}
