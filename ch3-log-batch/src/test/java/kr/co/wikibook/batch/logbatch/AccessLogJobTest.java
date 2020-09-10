package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBootTest("access-log=classpath:/sample-access-log.csv")
public class AccessLogJobTest {

  static File output = new File("user-access-summary.csv");

  @BeforeAll
  static void deleteOutput() {
    output.delete();
  }

  @Test
  void launchJob(@Autowired DataSource dataSource) {
    int count = JdbcTestUtils.countRowsInTable(new JdbcTemplate(dataSource), "access_log");
    assertThat(count).isGreaterThan(0);
    assertThat(output.exists()).isTrue();
  }
}
