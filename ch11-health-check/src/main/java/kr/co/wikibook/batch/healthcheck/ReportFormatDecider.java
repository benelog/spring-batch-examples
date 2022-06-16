package kr.co.wikibook.batch.healthcheck;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class ReportFormatDecider implements JobExecutionDecider {

  private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
    JobParameters jobParameters = jobExecution.getJobParameters();
    String reportDayParam = jobParameters.getString("reportDay");
    LocalDate reportDay = LocalDate.parse(reportDayParam, DAY_FORMATTER);
    ReportFormat reportFormat = getReportFormat(reportDay);
    return new FlowExecutionStatus(reportFormat.name());
  }

  private ReportFormat getReportFormat(LocalDate reportDay) {
    if (reportDay.getDayOfMonth() == 1) {
      return ReportFormat.MONTHLY;
    }

    if (reportDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
      return ReportFormat.WEEKLY;
    }

    return ReportFormat.DAILY;
  }
}
