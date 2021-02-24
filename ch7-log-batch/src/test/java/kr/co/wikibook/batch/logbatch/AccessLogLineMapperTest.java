package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.LineMapper;

class AccessLogLineMapperTest {

  @Test
  void mapLine() throws Exception {
    // given
    var jobConfig = new AccessLogJobConfig();
    var line = "2020-06-20 12:14:16,175.242.91.54,benelog";
    LineMapper<AccessLog> lineMapper = jobConfig.buildAccessLogLineMapper();

    // when
    AccessLog log = lineMapper.mapLine(line, 0);

    // then
    assertThat(log.getAccessDateTime()).isEqualTo("2020-06-20T12:14:16Z");
    assertThat(log.getIp()).isEqualTo("175.242.91.54");
    assertThat(log.getUsername()).isEqualTo("benelog");
  }
}
