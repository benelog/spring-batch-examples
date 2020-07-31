package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AccessLogLineMapperTest {
  @Test
  void mapLine() {
    // given
    var line = "2020-06-20 12:14:16,175.242.91.54,benelog";
    var lineMapper = new AccessLogLineMapper();

    // when
    AccessLog log = lineMapper.mapLine(line);

    // then
    assertThat(log.getAccessDateTime()).isEqualTo("2020-06-20T12:14:16Z");
    assertThat(log.getIp()).isEqualTo("175.242.91.54");
    assertThat(log.getUsername()).isEqualTo("benelog");
  }
}
