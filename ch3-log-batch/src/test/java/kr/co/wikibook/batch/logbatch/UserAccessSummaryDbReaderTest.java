package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.time.Instant;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = TestDbConfig.class)
class UserAccessSummaryDbReaderTest {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  DataSource dataSource;

  SimpleJdbcInsert insertStmt;

  @BeforeEach
  void setUp() {
    insertStmt = new SimpleJdbcInsert(dataSource);
    insertStmt.withTableName("access_log");
  }

  @Test
  void readRows() throws SQLException {
    // given
    insert(Instant.now(), "127.0.0.1", "benelog");
    insert(Instant.now(), "192.168.0.1", "benelog");
    insert(Instant.now(), "127.0.0.1", "jojoldu");
    var reader = new UserAccessSummaryDbReader(dataSource);

    // when
    reader.open();
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

  private void insert(Instant instant, String ip, String username) {
    var log = new AccessLog(instant, ip, username);
    insertStmt.execute(new BeanPropertySqlParameterSource(log));
  }
}
