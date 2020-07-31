package kr.co.wikibook.batch.logbatch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.core.io.Resource;

public class UserAccessSummaryCsvWriter implements ItemStreamWriter<UserAccessSummary> {

  private Logger log = LoggerFactory.getLogger(UserAccessSummaryCsvWriter.class);

  private final Resource resource;
  private final UserAccessSummaryLineAggregator lineAggregator = new UserAccessSummaryLineAggregator();
  private BufferedWriter lineWriter;

  public UserAccessSummaryCsvWriter(Resource resource) {
    this.resource = resource;
  }

  @Override
  public void write(List<? extends UserAccessSummary> items) throws Exception {
    for (UserAccessSummary item : items) {
      lineWriter.write(lineAggregator.aggregate(item));
    }
  }

  @Override
  public void open(ExecutionContext executionContext) {
    try {
      this.lineWriter = Files.newBufferedWriter(Paths.get(resource.getURI()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void update(ExecutionContext executionContext) {
  }

  @Override
  public void close() {
    try {
      lineWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.info("{} closed", resource);
  }
}