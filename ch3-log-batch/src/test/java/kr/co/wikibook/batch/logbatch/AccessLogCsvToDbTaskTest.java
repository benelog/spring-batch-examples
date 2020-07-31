package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

@SpringJUnitConfig(classes = TestDbConfig.class)
@Transactional
class AccessLogCsvToDbTaskTest {

  @Autowired
  DataSource dataSource;

  @Test
  public void runTask() throws Exception {
    // given
    var jobConfig = new AccessLogJobConfig(this.dataSource);
    var resource = new ClassPathResource("sample-access-log.csv");
    CommandLineRunner task = jobConfig.accessLogCsvToDbTask(resource);

    // when
    task.run();

    // then
    int count = JdbcTestUtils.countRowsInTable(new JdbcTemplate(dataSource), "access_log");
    assertThat(count).isGreaterThanOrEqualTo(3);
  }
}
