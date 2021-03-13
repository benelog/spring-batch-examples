package kr.co.wikibook.batch.logbatch;

import org.springframework.batch.item.file.transform.FieldExtractor;

public class UserAccessSummaryFieldSetExtractor implements FieldExtractor<UserAccessSummary> {

  public Object[] extract(UserAccessSummary summary) {
    return new Object[]{summary.getUsername(), summary.getAccessCount()};
  }
}
