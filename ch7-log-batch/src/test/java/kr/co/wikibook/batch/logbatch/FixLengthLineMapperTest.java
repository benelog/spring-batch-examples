package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;

class FixLengthLineMapperTest {
  @Test
  void mapLine() throws Exception {
    // given
    var line = "2020-06-20 12:14:16 175.242.91.54   benelog   ";
    LineMapper<AccessLog> lineMapper = this.buildAccessLogLineMapper();

    // when
    AccessLog log = lineMapper.mapLine(line, 0);

    // then
    assertThat(log.getAccessDateTime()).isEqualTo("2020-06-20T12:14:16Z");
    assertThat(log.getIp()).isEqualTo("175.242.91.54");
    assertThat(log.getUsername()).isEqualTo("benelog");
  }

  LineMapper<AccessLog> buildAccessLogLineMapper() {
    FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
    tokenizer.setColumns(new Range(1, 20), new Range(21, 36), new Range(37, 46));

    var lineMapper = new DefaultLineMapper<AccessLog>();
    lineMapper.setLineTokenizer(tokenizer);
    lineMapper.setFieldSetMapper(new AccessLogFieldSetMapper());
    return lineMapper;
  }
}
