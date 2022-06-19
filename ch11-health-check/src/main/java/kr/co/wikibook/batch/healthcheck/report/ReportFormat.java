package kr.co.wikibook.batch.healthcheck.report;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public enum ReportFormat {
  DAILY, WEEKLY, MONTHLY
}
