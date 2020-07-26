package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

class CountAccessLogTaskTest {

  @Test
  void countAccessLog() {
    var dataSource = buildDataSource();
    var task = new CountAccessLogTask(dataSource);
    task.run();
  }

  DataSource buildDataSource() {
    return new EmbeddedDatabaseBuilder()
        .setName("log-test-db")
        .setType(EmbeddedDatabaseType.H2)
        .addScript("schema.sql")
        .build();
  }
}
