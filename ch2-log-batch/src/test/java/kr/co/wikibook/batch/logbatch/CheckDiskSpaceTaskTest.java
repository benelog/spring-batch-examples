package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CheckDiskSpaceTaskTest {

  @Test
  void checkDiskSpace() {
    var task = new CheckDiskSpaceTask();
    assertThatThrownBy(() ->
        task.run("/", "100")
    ).isInstanceOf(IllegalStateException.class);
  }
}
