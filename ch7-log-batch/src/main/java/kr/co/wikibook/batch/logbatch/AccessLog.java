package kr.co.wikibook.batch.logbatch;

import java.beans.ConstructorProperties;
import java.time.Instant;

public class AccessLog {
  private final Instant accessDateTime;
  private final String ip;
  private final String username;

  @ConstructorProperties({"accessDateTime","ip","username"})
  public AccessLog(Instant accessDateTime, String ip, String username) {
    this.accessDateTime = accessDateTime;
    this.ip = ip;
    this.username = username;
  }

  public Instant getAccessDateTime() {
    return accessDateTime;
  }

  public String getIp() {
    return ip;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public String toString() {
    return String.format(
        "AccessLog{accessDateTime=%s,ip=%s,username=%s}", accessDateTime, ip, username);
  }
}
