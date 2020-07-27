package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CountAccessLogTask implements CommandLineRunner {

  private final JdbcTemplate jdbc;

  public CountAccessLogTask(DataSource dataSource) {
    this.jdbc = new JdbcTemplate(dataSource);
  }

  @Override
  public void run(String... args) {
    long count = jdbc.queryForObject("SELECT COUNT(1) FROM access_log", Long.class);
    System.out.println("access_log 테이블의 건 수 : " + count);
  }
}
