package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CountAccessLogTask implements CommandLineRunner {

  private final JdbcTemplate jdbc;
  private final Logger logger;

  public CountAccessLogTask(DataSource dataSource, ILoggerFactory loggerFactory) {
    this.jdbc = new JdbcTemplate(dataSource);
    this.logger = loggerFactory.getLogger(CountAccessLogTask.class.getName());
  }

  @Override
  public void run(String... args) {
    long count = jdbc.queryForObject("SELECT COUNT(1) FROM access_log", Long.class);
    logger.info("access_log 테이블의 건 수 : {}", count);
  }
}
