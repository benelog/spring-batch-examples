package kr.co.wikibook.batch.logbatch;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

public class DeleteOldAccessLogTask implements Tasklet {

  private LocalDate indexDay;
  private final NamedParameterJdbcTemplate jdbc;
  private final LocalDate endDay;

  public DeleteOldAccessLogTask(DataSource dataSource, LocalDate startDay, LocalDate endDay) {
    Assert.isTrue(!startDay.isAfter(endDay), "시작일은 종료일보다 같거나 작아야 한다.");
    this.jdbc = new NamedParameterJdbcTemplate(dataSource);
    this.indexDay = startDay;
    this.endDay = endDay;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    Instant from = indexDay.atStartOfDay().toInstant(ZoneOffset.UTC);
    Instant to = indexDay.plusDays(1L).atStartOfDay().toInstant(ZoneOffset.UTC);

    int deleted = jdbc.update(
        "DELETE FROM access_log WHERE access_date_time >= :from AND access_date_time < :to",
        Map.of("from", from, "to", to)
    );
    contribution.incrementWriteCount(deleted);
    indexDay = indexDay.plusDays(1L);
    return RepeatStatus.continueIf(!indexDay.isAfter(endDay));
  }
}
