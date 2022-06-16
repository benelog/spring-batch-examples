package kr.co.wikibook.batch.healthcheck;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class ReportFormatDecideTasklet extends StepExecutionListenerSupport implements Tasklet {

  private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
    String reportDayParam = (String) jobParameters.get("reportDay");
    LocalDate reportDay = LocalDate.parse(reportDayParam, DAY_FORMATTER);

    ReportFormat reportFormat = getReportFormat(reportDay);
    ExecutionContext executionContext = contribution.getStepExecution().getExecutionContext();
    executionContext.put("reportFormat", reportFormat);

    return RepeatStatus.FINISHED;
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

  public ExitStatus afterStep(StepExecution stepExecution) {
    ExecutionContext executionContext = stepExecution.getExecutionContext();
    ReportFormat reportType = (ReportFormat) executionContext.get("reportFormat");
    return new ExitStatus(reportType.name());
  }
}
