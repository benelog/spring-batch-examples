package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CountAccessLogTask implements Tasklet {

  private final JdbcTemplate jdbc;

  public CountAccessLogTask(DataSource dataSource) {
    this.jdbc = new JdbcTemplate(dataSource);
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    long count = jdbc.queryForObject("SELECT COUNT(1) FROM access_log", Long.class);

    StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
    ExecutionContext executionContext = stepExecution.getExecutionContext();
    executionContext.put("count", count);
    return RepeatStatus.FINISHED;
  }
}
