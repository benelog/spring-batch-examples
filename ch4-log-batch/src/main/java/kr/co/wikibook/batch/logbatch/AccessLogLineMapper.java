package kr.co.wikibook.batch.logbatch;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class AccessLogLineMapper {
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

  public AccessLog mapLine(String line) {
    String[] attrs = line.split(",");
    Instant accessDateTime = Instant.from(FORMATTER.parse(attrs[0]));
    String ip = attrs[1];
    String username = attrs[2];
    return new AccessLog(accessDateTime, ip, username);
  }
}
