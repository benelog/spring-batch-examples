package kr.co.wikibook.batch.logbatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class AccessLogCsvReader {

  private final AccessLogLineMapper lineMapper = new AccessLogLineMapper(); // <2>
  private final Resource resource;
  private BufferedReader bufferedReader; // <1>

  public AccessLogCsvReader(Resource resource) { // <3>
    this.resource = resource;
  }

  public void open() throws IOException { // <4>
    InputStream inputStream = resource.getInputStream();
    this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    bufferedReader = new BufferedReader(new FileReader(inputFile));

  }

  @Nullable // <5>
  public AccessLog read() throws IOException {
    String line = this.bufferedReader.readLine();
    if (line == null) {
      return null;
    }
    return this.lineMapper.mapLine(line);
  }

  public void close() throws IOException { // <6>
    this.bufferedReader.close();
  }

  private boolean isCsvFile(Path path)  {
    File inputFile = path.toFile();
    try ( var lineReader = new BufferedReader(new FileReader(inputFile)) ) {

      String line = null;

      while ((line = lineReader.readLine()) != null) {
        if (!line.contains(",")) {
          return false;
        }
        if (!StringUtils.isAsciiPrintable(row)) {
          return false;
        }
      }
    }

    return true;
  }

}
