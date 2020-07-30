package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserAccessSummaryLineAggregatorTest {
  @Test
  void aggregate() {
    // given
    var lineAggregator = new UserAccessSummaryLineAggregator();
    UserAccessSummary summary = new UserAccessSummary("benelog", 3);

    // when
    String line = lineAggregator.aggregate(summary);

    //then
    assertThat(line).isEqualTo("benelog,3\n");
  }
}
