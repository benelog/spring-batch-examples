package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PrintTableTask implements Tasklet {
  private final JdbcTemplate db;
  public PrintTableTask(DataSource dataSource) {
      this.db = new JdbcTemplate(dataSource);
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    db.queryForList("SHOW TABLES").forEach(System.out::println);
    return RepeatStatus.FINISHED;
  }
}
