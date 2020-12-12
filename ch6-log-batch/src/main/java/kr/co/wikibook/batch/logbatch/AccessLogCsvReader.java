package kr.co.wikibook.batch.logbatch;

import java.io.Closeable;
import java.io.IOException;
import java.util.Scanner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class AccessLogCsvReader implements ItemStreamReader<AccessLog> {
  private Scanner scanner;
  private final AccessLogLineMapper lineMapper = new AccessLogLineMapper();
  private final Resource resource;

  public AccessLogCsvReader(Resource resource)  {
    this.resource = resource;
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    try {
      this.scanner = new Scanner(resource.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Nullable
  public AccessLog read() {
    if (this.scanner.hasNext()) {
      return this.lineMapper.mapLine(this.scanner.nextLine());
    }
    return null;
  }

  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {
  }

  @Override
  public void close() {
    this.scanner.close();
  }
}