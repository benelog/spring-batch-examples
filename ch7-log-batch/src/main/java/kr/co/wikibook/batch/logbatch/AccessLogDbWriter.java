package kr.co.wikibook.batch.logbatch;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class AccessLogDbWriter implements ItemWriter<AccessLog> {
  private final String INSERT_STMT =
      "INSERT INTO access_log(access_date_time, ip, username)"
          + "VALUES (:accessDateTime, :ip, :username)";

  private final NamedParameterJdbcTemplate jdbc;

  public AccessLogDbWriter(DataSource dataSource) {
    this.jdbc = new NamedParameterJdbcTemplate(dataSource);
  }

  @Override
  public void write(List<? extends AccessLog> items) {
    BeanPropertySqlParameterSource[] params =
        items.stream()
            .map(BeanPropertySqlParameterSource::new)
            .toArray(BeanPropertySqlParameterSource[]::new);
    this.jdbc.batchUpdate(INSERT_STMT, params);
  }
}
