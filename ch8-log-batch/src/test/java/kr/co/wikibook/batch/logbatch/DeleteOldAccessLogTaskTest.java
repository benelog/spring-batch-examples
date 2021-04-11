package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

@SpringJUnitConfig(TestDbConfig.class)
@Transactional
class DeleteOldAccessLogTaskTest {
  @Autowired
  DataSource dataSource;

  @Test
  void invalidPeriod() {
    // given
    var startDay = LocalDate.of(2021, 4, 3);
    var endDay = LocalDate.of(2021, 4, 2);

    // when, then
    assertThrows(IllegalArgumentException.class, () -> {
      new DeleteOldAccessLogTask(this.dataSource, startDay, endDay);
    });
  }

  @Test
  void deleteAccessLogs() {
    // given
    var jdbc = new NamedParameterJdbcTemplate(this.dataSource);
    var log1 = new AccessLog(Instant.parse("2021-04-01T11:14:16Z"), "192.168.0.1", "benelog");
    jdbc.update(AccessLogSql.INSERT, new BeanPropertySqlParameterSource(log1));
    var log2 = new AccessLog(Instant.parse("2021-04-02T11:14:16Z"), "192.168.0.1", "benelog");
    jdbc.update(AccessLogSql.INSERT, new BeanPropertySqlParameterSource(log2));

    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
    var stepContribution = new StepContribution(stepExecution);
    var chunkContext = new ChunkContext(new StepContext(stepExecution));
    var startDay = LocalDate.of(2021, 4, 1);
    var endDay = LocalDate.of(2021, 4, 2);

    // when
    var task = new DeleteOldAccessLogTask(this.dataSource, startDay, endDay);
    RepeatStatus status = RepeatStatus.CONTINUABLE;
    while (status != RepeatStatus.FINISHED) {
      status = task.execute(stepContribution, chunkContext);
    }

    // then
    assertThat(stepContribution.getWriteCount()).isEqualTo(2);
  }
}
