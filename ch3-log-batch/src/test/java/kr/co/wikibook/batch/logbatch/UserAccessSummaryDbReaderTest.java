package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

@SpringJUnitConfig(TestDbConfig.class)
@Transactional
class UserAccessSummaryDbReaderTest {

  Logger logger = LoggerFactory.getLogger(this.getClass());
  static final String INSERT = "INSERT INTO access_log (access_date_time, ip, username) VALUES ";

  @Autowired
  DataSource dataSource;

  SimpleJdbcInsert insertStmt;
  UserAccessSummaryDbReader reader;

  @BeforeEach
  void setUp() {
    this.insertStmt = new SimpleJdbcInsert(dataSource);
    this.insertStmt.withTableName("access_log");
    this.reader = new UserAccessSummaryDbReader(dataSource);
  }

  void tearDown() {
    this.reader.close();
  }

  @Test
  @Sql(statements = {
      INSERT + "('2020-06-10 11:14', '175.242.91.54', 'benelog')",
      INSERT + "('2020-06-10 11:14', '192.168.0.1', 'benelog')",
      INSERT + "('2020-06-10 11:14', '192.168.0.3', 'jojoldu')"
  })
  void readRows() throws SQLException {
    // when
    this.reader.open();
    UserAccessSummary item1 = this.reader.read();
    UserAccessSummary item2 = this.reader.read();
    UserAccessSummary item3 = this.reader.read();

    // then
    assertThat(item1.getUsername()).isEqualTo("benelog");
    assertThat(item1.getAccessCount()).isEqualTo(2);
    assertThat(item2.getUsername()).isEqualTo("jojoldu");
    assertThat(item2.getAccessCount()).isEqualTo(1);
    assertThat(item3).isNull();
  }
}
