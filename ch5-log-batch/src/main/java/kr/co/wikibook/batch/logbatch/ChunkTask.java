package kr.co.wikibook.batch.logbatch;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public abstract class ChunkTask<I> implements Tasklet {

  protected int chunkSize = 100;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws SQLException, IOException {
    List<I> chunk = new LinkedList<>();
    while (true) {
      I item = read();
      if (item == null) {
        write(chunk);
        contribution.incrementWriteCount(chunk.size());
        return RepeatStatus.FINISHED;
      }
      contribution.incrementReadCount();
      chunk.add(item);
      if (chunk.size() == this.chunkSize) {
        write(chunk);
        contribution.incrementWriteCount(chunk.size());
        return RepeatStatus.CONTINUABLE;
      }
    }
  }

  @PostConstruct
  abstract void open() throws IOException, SQLException;

  abstract I read() throws SQLException;

  abstract void write(List<I> items) throws IOException;

  @PreDestroy
  abstract void close() throws IOException;
}
