package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.core.io.ClassPathResource;

class AccessLogCsvReaderTest {
  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Test
  void readLines() throws IOException {
    // given
    var resource = new ClassPathResource("sample-access-log.csv");
    var reader = new AccessLogCsvReader(resource);

    // when
    reader.open(new ExecutionContext());
    int itemCount = 0;
    AccessLog item;
    while ((item = reader.read()) != null) {
      itemCount++;
      logger.debug("{}", item);
    }
    reader.close();

    // then
    assertThat(itemCount).isEqualTo(3);
  }

  @Test
  void instanceOfItemStream() {
    var config = new AccessLogJobConfig(null, null, null);
    ItemReader<AccessLog> accessLogCsvReader = config.accessLogCsvReader(null);
    assertThat(accessLogCsvReader).isInstanceOf(ItemStream.class);
  }
}
