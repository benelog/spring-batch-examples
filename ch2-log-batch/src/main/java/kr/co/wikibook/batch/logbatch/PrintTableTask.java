package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PrintTableTask implements CommandLineRunner {
  private final JdbcTemplate db;
  public PrintTableTask(DataSource dataSource) {
      this.db = new JdbcTemplate(dataSource);
  }

  @Override
  public void run(String... args) throws Exception {
    db.queryForList("SHOW TABLES").forEach(System.out::println);
  }
}
