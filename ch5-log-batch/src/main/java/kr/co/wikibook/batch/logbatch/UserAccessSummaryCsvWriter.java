package kr.co.wikibook.batch.logbatch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.core.io.Resource;

public class UserAccessSummaryCsvWriter {
  private final Resource resource;
  private final UserAccessSummaryLineAggregator lineAggregator = new UserAccessSummaryLineAggregator();
  private BufferedWriter lineWriter;

  public UserAccessSummaryCsvWriter(Resource resource) {
    this.resource = resource;
  }

  public void write(List<UserAccessSummary> items) throws IOException {
    for (UserAccessSummary item : items) {
      lineWriter.write(lineAggregator.aggregate(item));
    }
  }

  public void open() throws IOException {
    this.lineWriter = Files.newBufferedWriter(Paths.get(resource.getURI()));
  }

  public void close() throws IOException {
    lineWriter.close();
  }
}
