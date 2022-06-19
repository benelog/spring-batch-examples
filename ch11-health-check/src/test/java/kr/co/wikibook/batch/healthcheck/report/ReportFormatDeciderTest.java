package kr.co.wikibook.batch.healthcheck.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import kr.co.wikibook.batch.healthcheck.report.ReportFormat;
import kr.co.wikibook.batch.healthcheck.report.ReportFormatDecider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;

class ReportFormatDeciderTest {

  @ParameterizedTest
  @MethodSource("provideReportDayAndFormat")
  void executeAndAfterStep(String reportDay, ReportFormat reportFormat) {
    // given
    var decider = new ReportFormatDecider();
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("reportDay", reportDay)
        .toJobParameters();
    JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution(
        "createReportJob", 1L, 1L, jobParameters
    );
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobExecution, "decideStep",  1L);

    // when
    FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

    // then
    assertThat(status.getName()).isEqualTo(reportFormat.name());
  }

  static Stream<Arguments> provideReportDayAndFormat() {
    return Stream.of(
        Arguments.of("2022-06-13", ReportFormat.DAILY),
        Arguments.of("2022-06-19", ReportFormat.WEEKLY),
        Arguments.of("2022-07-01", ReportFormat.MONTHLY)
    );
  }
}