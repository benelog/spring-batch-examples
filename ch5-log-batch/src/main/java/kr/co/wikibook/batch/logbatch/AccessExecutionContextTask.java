package kr.co.wikibook.batch.logbatch;

import java.util.concurrent.Callable;
import org.springframework.batch.repeat.RepeatStatus;

public class AccessExecutionContextTask implements Callable<RepeatStatus> {

  @Override
  public RepeatStatus call() throws Exception {
    System.out.println("hihi");
    return RepeatStatus.FINISHED;
  }
}
