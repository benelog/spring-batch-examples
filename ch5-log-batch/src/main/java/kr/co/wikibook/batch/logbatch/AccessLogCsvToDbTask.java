package kr.co.wikibook.batch.logbatch;

import java.io.IOException;
import java.util.List;

public class AccessLogCsvToDbTask extends ChunkTask<AccessLog> {
  private AccessLogCsvReader reader;
  private AccessLogDbWriter writer;

  public AccessLogCsvToDbTask(AccessLogCsvReader reader, AccessLogDbWriter writer) {
    this.reader = reader;
    this.writer = writer;
    this.chunkSize = 300;
  }

  @Override
  void open() throws IOException {
    reader.open();
  }

  @Override
  AccessLog read() {
    return this.reader.read();
  }

  @Override
  void write(List items) {
    this.writer.write(items);
  }

  @Override
  void close() {
    this.reader.close();
  }
}
