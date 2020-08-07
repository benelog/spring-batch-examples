package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CheckDiskSpaceTaskTest {

  CheckDiskSpaceTask task = new CheckDiskSpaceTask();

  @DisplayName("디스크 용량이 기대치보다 많다")
  @Test
  void checkDiskSpaceWhenSufficient() {
    task.run("/", "1");
  }

  @DisplayName("디스크 용량이 기대치보다 적다")
  @Test
  void checkDiskSpaceWhenInsufficient() {
    assertThatThrownBy(() ->
        task.run("/", "100")
    ).isInstanceOf(IllegalStateException.class);
  }
}
