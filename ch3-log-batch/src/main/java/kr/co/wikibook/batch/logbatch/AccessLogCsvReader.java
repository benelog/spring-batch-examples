package kr.co.wikibook.batch.logbatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class AccessLogCsvReader {

  private final AccessLogLineMapper lineMapper = new AccessLogLineMapper();
  private final Resource resource;
  private BufferedReader bufferedReader;

  public AccessLogCsvReader(Resource resource) {
    this.resource = resource;
  }

  public void open() throws IOException {
    InputStream inputStream = resource.getInputStream();
    this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
  }

  @Nullable
  public AccessLog read() throws IOException {
    String line = this.bufferedReader.readLine();
    if (line == null) {
      return null;
    }
    return this.lineMapper.mapLine(line);
  }

  public void close() throws IOException {
    this.bufferedReader.close();
  }
}
