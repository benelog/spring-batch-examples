package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import slf4jtest.LogMessage;
import slf4jtest.Settings;
import slf4jtest.TestLoggerFactory;

class CountAccessLogTaskTest {

  @Test
  void countAccessLog() {
    // given
    TestLoggerFactory loggerFactory = Settings.instance()
        .enableAll()
        .buildLogging();
    DataSource dataSource = buildDataSource();
    var task = new CountAccessLogTask(dataSource, loggerFactory);

    // when
    task.run();

    // then
    LogMessage logMessage = loggerFactory.lines().iterator().next();
    assertThat(logMessage.text).isEqualTo("access_log 테이블의 건 수 : 0");
  }

  private DataSource buildDataSource() {
    return new EmbeddedDatabaseBuilder()
        .setName("log-test-db")
        .setType(EmbeddedDatabaseType.H2)
        .addScript("schema.sql")
        .build();
  }
}
