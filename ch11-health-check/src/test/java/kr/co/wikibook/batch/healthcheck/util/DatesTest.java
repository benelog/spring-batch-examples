package kr.co.wikibook.batch.healthcheck.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DatesTest {

  @ParameterizedTest
  @MethodSource("provideHolidays")
  void isHoliday(LocalDate day) {
    boolean actual = Dates.isHoliday(day);
    assertThat(actual).isTrue();
  }

  @ParameterizedTest
  @MethodSource("provideWorkingDays")
  void isWorkingDay(LocalDate day) {
    boolean actual = Dates.isHoliday(day);
    assertThat(actual).isFalse();
  }

  static Stream<LocalDate> provideHolidays() {
    return Stream.of(
        LocalDate.of(2022, 6, 4), // 토요일
        LocalDate.of(2022, 6, 5), // 일요일
        LocalDate.of(2020, 12, 25), // 크리스마스
        LocalDate.of(2020, 5, 5) // 어린이날
    );
  }

  static Stream<LocalDate> provideWorkingDays() {
    return Stream.of(
        LocalDate.of(2020, 7, 17),
        LocalDate.of(2022, 4, 5),
        LocalDate.of(2022, 6, 7)
    );
  }
}
