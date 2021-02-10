package kr.co.wikibook.batch.logbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;

public class HelloItemStream implements ItemStream {
  private final Logger logger = LoggerFactory.getLogger(HelloItemStream.class);
  private static final String COUNT_KEY = "chunkCount";
  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    executionContext.putInt(COUNT_KEY, 0);
  }

  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {
    int count = executionContext.getInt(COUNT_KEY);
    executionContext.putInt(COUNT_KEY, ++count);
    logger.info("chunk count {}" , count);
  }

  @Override
  public void close() throws ItemStreamException {
    logger.info("close");
  }
}
