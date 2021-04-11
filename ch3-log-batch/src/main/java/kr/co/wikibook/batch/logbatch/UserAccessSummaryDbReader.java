package kr.co.wikibook.batch.logbatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;

public class UserAccessSummaryDbReader {
  private final RowMapper<UserAccessSummary> rowMapper = new DataClassRowMapper<>(UserAccessSummary.class);

  private final DataSource dataSource;
  private PreparedStatement stmt;
  private Connection con;
  private ResultSet resultSet;
  private int rowCount = 0;

  public UserAccessSummaryDbReader(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void open() throws SQLException {
    this.con = DataSourceUtils.getConnection(dataSource);
    this.stmt = con.prepareStatement(
        AccessLogSql.COUNT_GROUP_BY_USERNAME,
        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY
    );
    this.resultSet = stmt.executeQuery();
  }

  @Nullable
  public UserAccessSummary read() throws SQLException {
    if (this.resultSet.next()) {
      UserAccessSummary item = this.rowMapper.mapRow(resultSet, rowCount);
      this.rowCount++;
      return item;
    }
    return null;
  }

  public void close() {
    this.rowCount = 0;
    JdbcUtils.closeResultSet(this.resultSet);
    JdbcUtils.closeStatement(this.stmt);
    JdbcUtils.closeConnection(this.con);
  }
}
