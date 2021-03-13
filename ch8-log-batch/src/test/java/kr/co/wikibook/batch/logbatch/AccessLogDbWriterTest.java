package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

@SpringJUnitConfig(TestDbConfig.class)
@Transactional
class AccessLogDbWriterTest {

  @Test
  public void write(@Autowired DataSource dataSource) throws Exception {
    // given
    JdbcBatchItemWriter<AccessLog> writer = new AccessLogJobConfig().buildAccessLogWriter(dataSource);
    var item = new AccessLog(Instant.now(), "127.0.0.1", "benelog");

    // when
    writer.write(List.of(item));

    // then
    int count = JdbcTestUtils.countRowsInTableWhere(
        new JdbcTemplate(dataSource),
        "access_log",
        "ip='127.0.0.1' AND username='benelog'"
    );
    assertThat(count).isEqualTo(1);
  }
}
