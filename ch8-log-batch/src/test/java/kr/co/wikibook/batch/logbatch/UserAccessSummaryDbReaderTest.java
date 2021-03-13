package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(TestDbConfig.class)
class UserAccessSummaryDbReaderTest {

  static final String INSERT = "INSERT INTO access_log (access_date_time, ip, username) VALUES ";

  @Test
  @Sql(statements = {
      INSERT + "('2020-06-10 11:14', '175.242.91.54', 'benelog')",
      INSERT + "('2020-06-10 11:14', '192.168.0.1', 'benelog')",
      INSERT + "('2020-06-10 11:14', '192.168.0.3', 'jojoldu')"
  })
  void readItems(@Autowired DataSource dataSource) throws Exception {
    // given
    var jobConfig = new AccessLogJobConfig();
    JdbcCursorItemReader<UserAccessSummary> reader = jobConfig
        .buildUserAccessSummaryReader(dataSource);

    // when
    reader.afterPropertiesSet();

    reader.open(new ExecutionContext());
    UserAccessSummary item1 = reader.read();
    UserAccessSummary item2 = reader.read();
    UserAccessSummary item3 = reader.read();
    reader.close();

    // then
    assertThat(item1.getUsername()).isEqualTo("benelog");
    assertThat(item1.getAccessCount()).isEqualTo(2);
    assertThat(item2.getUsername()).isEqualTo("jojoldu");
    assertThat(item2.getAccessCount()).isEqualTo(1);
    assertThat(item3).isNull();
  }
}
