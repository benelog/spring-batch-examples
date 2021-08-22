package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import slf4jtest.Settings;
import slf4jtest.TestLoggerFactory;

class CheckDiskSpaceTaskTest {

  TestLoggerFactory loggerFactory = Settings.instance()
      .enableAll()
      .buildLogging();
  CheckDiskSpaceTask task = new CheckDiskSpaceTask(loggerFactory);

  @DisplayName("지정된 디렉토리가 없으면 아무것도 하지 않는다.")
  @Test
  void doNothingWhenEmptyArgument() {
    task.run();
  }

  @DisplayName("디스크 용량이 기대치보다 많다")
  @Test
  void checkDiskSpaceWhenSufficient() {
    task.run("/", "1");
    assertThat(loggerFactory.matches("남은 용량 \\d{1,3}%")).isTrue();
  }

  @DisplayName("디스크 용량이 기대치보다 적다")
  @Test
  void checkDiskSpaceWhenInsufficient() {
    assertThatThrownBy(() ->
        task.run("/", "100")
    ).isInstanceOf(IllegalStateException.class);
    assertThat(loggerFactory.matches("남은 용량 \\d{1,3}%")).isTrue();
  }
}
