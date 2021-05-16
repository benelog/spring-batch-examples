package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcLimitedUpdateTasklet implements Tasklet {
  private final JdbcTemplate jdbc;
  private final String sql;

  public JdbcLimitedUpdateTasklet(DataSource dataSource, String sql, int limitSize) {
    this.jdbc = new JdbcTemplate(dataSource);
    this.sql = sql + " LIMIT " + limitSize;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    int affected = jdbc.update(sql);
    contribution.incrementWriteCount(affected);
    return RepeatStatus.continueIf(affected > 0);
  }
}
