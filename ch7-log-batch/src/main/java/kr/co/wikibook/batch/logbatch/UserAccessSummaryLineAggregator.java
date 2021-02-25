package kr.co.wikibook.batch.logbatch;

import org.springframework.batch.item.file.transform.LineAggregator;

public class UserAccessSummaryLineAggregator implements LineAggregator<UserAccessSummary> {
  public String aggregate(UserAccessSummary summary) {
    return String.format("%s,%d", summary.getUsername(), summary.getAccessCount());
  }
}
