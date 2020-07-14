package kr.co.wikibook.batch.logbatch;

import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

public class AccessLogCsvToDbTask implements CommandLineRunner {
  private AccessLogCsvReader reader;
  private AccessLogDbWriter writer;
  private int chunkSize = 100;
  private final Logger log = LoggerFactory.getLogger(AccessLogCsvToDbTask.class);

  public AccessLogCsvToDbTask(AccessLogCsvReader reader, AccessLogDbWriter writer, int chunkSize) {
    this.reader = reader;
    this.writer = writer;
    this.chunkSize = chunkSize;
  }

  @Override
  public void run(String... args) throws Exception {
    int totalItems = 0;
    this.reader.open();
    List<AccessLog> chunk = new LinkedList<>();

    while (true) {
      AccessLog item = this.reader.read();

      if (item == null) {
        if (chunk.size() > 0) {
          this.writer.write(chunk);
        }
        break;
      }

      totalItems++;
      chunk.add(item);
      if (chunk.size() == this.chunkSize) {
        this.writer.write(chunk);
        chunk.clear();
      }
    }

    this.reader.close();
    log.info("{}개의 항목이 처리됨", totalItems);
  }
}
