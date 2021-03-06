package kr.co.wikibook.batch.logbatch;

import java.util.LinkedList;
import java.util.List;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public abstract class SimpleChunkTask<I> implements Tasklet {

  protected int chunkSize = 100;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
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

  abstract void open();

  abstract I read();

  abstract void write(List<I> items);

  abstract void close();
}
