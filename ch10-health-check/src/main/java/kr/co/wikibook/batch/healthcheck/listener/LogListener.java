package kr.co.wikibook.batch.healthcheck.listener;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.lang.Nullable;

public class LogListener<T, S> extends StepListenerSupport<T, S> {

  private final Logger logger = LoggerFactory.getLogger(LogListener.class);

  @Override
  public void beforeStep(StepExecution stepExecution) {
    logger.info("beforeStep");
  }

  @Override
  public void beforeChunk(ChunkContext context) {
    logger.info("beforeChunk");
  }

  @Override
  public void beforeRead() {
    logger.info("beforeRead");
  }

  @Override
  public void afterRead(T item) {
    logger.info("afterRead. item={}", item);
  }

  @Override
  public void beforeProcess(T item) {
    logger.info("beforeProcess. item={}", item);
  }

  @Override
  public void afterProcess(T item, @Nullable S result) {
    logger.info("afterProcess. {} -> {}", item, result);
  }

  @Override
  public void beforeWrite(List<? extends S> items) {
    logger.info("beforeWrite. items={}", items);
  }

  @Override
  public void afterWrite(List<? extends S> items) {
    logger.info("afterWrite. items={}", items);
  }

  @Override
  public void afterChunk(ChunkContext context) {
    logger.info("afterChunk");
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    logger.info("afterStep");
    return ExitStatus.COMPLETED;
  }

  @Override
  public void afterChunkError(ChunkContext context) {
    logger.info("afterChunkError");
  }
}
