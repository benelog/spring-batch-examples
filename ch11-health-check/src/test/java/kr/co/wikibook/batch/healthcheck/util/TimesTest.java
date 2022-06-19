package kr.co.wikibook.batch.healthcheck.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import kr.co.wikibook.batch.healthcheck.support.Times;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TimesTest {
  @ParameterizedTest
  @CsvSource({
      "2022-01-30T13:00:00Z, 2022-01-30T13:01:00Z, 0:01:00",
      "2022-01-30T13:00:00Z, 2022-01-30T14:01:50Z, 1:01:50",
      "2022-01-30T13:40:40Z, 2022-01-30T14:10:21Z, 0:29:41"
  }
  )
  void getReadableDuration(String from, String to, String expected) {
    Instant fromTime = Instant.parse(from);
    Instant toTime = Instant.parse(to);
    String actual = Times.getReadableDuration(fromTime, toTime);
    assertThat(actual).isEqualTo(expected);
  }
}
