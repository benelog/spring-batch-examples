package kr.co.wikibook.batch.healthcheck;

import java.net.URI;
import java.net.URL;

public class ResponseStatus {

  private final URI uri;
  private final int statusCode;
  private final long responseTime; // milliseconds

  public ResponseStatus(URI uri, int statusCode, long responseTime) {
    this.uri = uri;
    this.statusCode = statusCode;
    this.responseTime = responseTime;
  }

  public URI getUri() {
    return uri;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public long getResponseTime() {
    return responseTime;
  }

  @Override
  public String toString() {
    return "ResponseStatus{" +
        "uri=" + uri +
        ", statusCode=" + statusCode +
        ", responseTime=" + responseTime +
        '}';
  }
}
