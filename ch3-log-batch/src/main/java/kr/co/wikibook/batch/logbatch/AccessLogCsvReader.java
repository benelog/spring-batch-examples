package kr.co.wikibook.batch.logbatch;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.Scanner;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class AccessLogCsvReader implements Closeable {
  private Scanner scanner; // <1>
  private final AccessLogLineMapper lineMapper = new AccessLogLineMapper(); // <2>
  private final Resource resource;

  public AccessLogCsvReader(Resource resource)  { // <3>
    this.resource = resource;
  }

  public void open() throws IOException { // <4>
    var inputStream = new BufferedInputStream(resource.getInputStream());
    this.scanner = new Scanner(inputStream);
  }

  @Nullable // <5>
  public AccessLog read() {
    if (this.scanner.hasNext()) {
      return this.lineMapper.mapLine(this.scanner.nextLine());
    }
    return null;
  }

  @Override
  public void close() { // <6>
    this.scanner.close();
  }
}
