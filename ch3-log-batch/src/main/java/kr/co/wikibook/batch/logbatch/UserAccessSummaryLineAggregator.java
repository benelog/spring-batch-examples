package kr.co.wikibook.batch.logbatch;

public class UserAccessSummaryLineAggregator {
  public String aggregate(UserAccessSummary summary) {
    return String.format("%s,%d", summary.getUsername(), summary.getAccessCount());
  }
}
