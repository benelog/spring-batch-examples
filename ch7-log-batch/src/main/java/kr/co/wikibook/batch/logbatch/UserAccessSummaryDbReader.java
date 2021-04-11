package kr.co.wikibook.batch.logbatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;

public class UserAccessSummaryDbReader implements ItemStreamReader<UserAccessSummary> {

  private final RowMapper<UserAccessSummary> rowMapper = new DataClassRowMapper<>(UserAccessSummary.class);

  private final DataSource dataSource;
  private PreparedStatement stmt;
  private Connection con;
  private ResultSet resultSet;
  private int rowCount = 0;

  public UserAccessSummaryDbReader(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void open(ExecutionContext executionContext) {
    this.con = DataSourceUtils.getConnection(dataSource);
    try {
      this.stmt = con.prepareStatement(
          AccessLogSql.COUNT_GROUP_BY_USERNAME,
          ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY
      );
      this.resultSet = stmt.executeQuery();
    } catch (SQLException e) {
      close();
      throw new RuntimeException(e);
    }
  }

  @Nullable
  public UserAccessSummary read() throws SQLException {
    if (resultSet.next()) {
      UserAccessSummary item = this.rowMapper.mapRow(resultSet, rowCount);
      rowCount++;
      return item;
    }
    return null;
  }

  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {
  }

  @Override
  public void close() {
    this.rowCount = 0;
    JdbcUtils.closeResultSet(this.resultSet);
    JdbcUtils.closeStatement(this.stmt);
    JdbcUtils.closeConnection(this.con);
  }
}

