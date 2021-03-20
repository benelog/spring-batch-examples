package kr.co.wikibook.batch.logbatch;

public class UserAccessSummary {

  private final String username;
  private final int accessCount;

  public UserAccessSummary(String username, int accessCount) {
    this.username = username;
    this.accessCount = accessCount;
  }

  public String getUsername() {
    return username;
  }

  public int getAccessCount() {
    return accessCount;
  }

  @Override
  public String toString() {
    return "{username=" + username + ", accessCount=" + accessCount + "}";
  }
}
