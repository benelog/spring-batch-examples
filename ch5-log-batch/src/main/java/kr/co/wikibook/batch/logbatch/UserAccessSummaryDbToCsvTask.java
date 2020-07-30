package kr.co.wikibook.batch.logbatch;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserAccessSummaryDbToCsvTask extends ChunkTask<UserAccessSummary> {

  private UserAccessSummaryDbReader reader;
  private UserAccessSummaryCsvWriter writer;

  public UserAccessSummaryDbToCsvTask(
      UserAccessSummaryDbReader reader,
      UserAccessSummaryCsvWriter writer) {
    this.reader = reader;
    this.writer = writer;
  }

  @Override
  void open() throws SQLException, IOException {
    reader.open();
    writer.open();
  }

  @Override
  UserAccessSummary read() throws SQLException {
    return this.reader.read();
  }

  @Override
  void write(List items) throws IOException {
    this.writer.write(items);
  }

  @Override
  void close() throws IOException {
    this.reader.close();
    this.writer.close();
  }
}
