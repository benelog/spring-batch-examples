package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBootTest("access-log=classpath:/sample-access-log.csv")
public class AccessLogJobTest {

  @Autowired
  DataSource dataSource;

  @Test
  void launchJob() {
    int count = JdbcTestUtils.countRowsInTable(new JdbcTemplate(this.dataSource), "access_log");
    assertThat(count).isGreaterThan(0);
  }
}
