package kr.co.wikibook.batch.logbatch.metadata;

import java.time.Instant;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JobMetadataDao {
  private final NamedParameterJdbcTemplate jdbcOperations;
  private final JobMetadataSql sql;

  public JobMetadataDao(DataSource dataSource, String tablePrefix) {
    this.jdbcOperations = new NamedParameterJdbcTemplate(dataSource);
    this.sql = new JobMetadataSql(tablePrefix);
  }

  public Long selectMaxJobExecutionIdBefore(Instant createTime) {
    return jdbcOperations.queryForObject(
        sql.selectMaxJobExecutionIdBefore(),
        Map.of("createTime", createTime), Long.class
    );
  }

  public Long selectMaxJobInstanceId(long jobExecutionId) {
    return jdbcOperations.queryForObject(
        sql.selectMaxJobInstanceId(),
        Map.of("jobExecutionId", jobExecutionId), Long.class
    );
  }

  public Long selectMaxStepExecutionId(long jobExecutionId) {
    return jdbcOperations.queryForObject(
        sql.selectMaxStepExecutionId(),
        Map.of("jobExecutionId", jobExecutionId), Long.class
    );
  }
}
