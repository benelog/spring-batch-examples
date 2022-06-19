package kr.co.wikibook.batch.healthcheck.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import kr.co.wikibook.batch.healthcheck.report.ReportFormat;
import kr.co.wikibook.batch.healthcheck.report.ReportFormatDecideTasklet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;

class ReportFormatDecideTaskletTest {


  @ParameterizedTest
  @MethodSource("provideReportDayAndFormat")
  void executeAndAfterStep(String reportDay, ReportFormat reportFormat) {
    // given
    var tasklet = new ReportFormatDecideTasklet();
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("reportDay", reportDay)
        .toJobParameters();
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);
    var stepContribution = new StepContribution(stepExecution);
    var chunkContext = new ChunkContext(new StepContext(stepExecution));

    // when
    RepeatStatus repeatStatus = tasklet.execute(stepContribution, chunkContext);
    ExitStatus exitStatus = tasklet.afterStep(stepExecution);

    // then
    assertThat(repeatStatus).isEqualTo(RepeatStatus.FINISHED);
    assertThat(exitStatus.getExitCode()).isEqualTo(reportFormat.name());
  }

  static Stream<Arguments> provideReportDayAndFormat() {
    return Stream.of(
        Arguments.of("2022-06-13", ReportFormat.DAILY),
        Arguments.of("2022-06-19", ReportFormat.WEEKLY),
        Arguments.of("2022-07-01", ReportFormat.MONTHLY)
    );
  }
}