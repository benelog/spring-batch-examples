package kr.co.wikibook.batch.healthcheck.listener;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LongSummaryStatistics;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.util.StopWatch;

public class StopWatchReporter implements StepExecutionListener, ItemProcessListener {

  private StopWatch stopWatch;

  @Override
  public void beforeStep(StepExecution stepExecution) {
    stopWatch = new StopWatch(stepExecution.getStepName());
  }


  @Override
  public void beforeProcess(Object item) {
    stopWatch.start(item.toString());
  }

  @Override
  public void afterProcess(Object item, Object result) {
    stopWatch.stop();
  }

  @Override
  public void onProcessError(Object item, Exception e) {
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    StopWatch.TaskInfo[] taskInfo = stopWatch.getTaskInfo();

    LongSummaryStatistics stats = Arrays.stream(taskInfo)
        .mapToLong(StopWatch.TaskInfo::getTimeMillis)
        .summaryStatistics();

    StopWatch.TaskInfo longestTask = Arrays.stream(taskInfo)
        .max(Comparator.comparing(StopWatch.TaskInfo::getTimeMillis))
        .get();

    System.out.println("----------------------------");
    System.out.printf("Call count : %d %n", stats.getCount());
    System.out.printf("Average time : %f ms%n", stats.getAverage());
    System.out.printf("Min time : %d ms%n", stats.getMin());
    System.out.printf("Max time : %d ms ( %s ) %n", stats.getMax(), longestTask.getTaskName());
    System.out.println(stopWatch.prettyPrint());

    return ExitStatus.COMPLETED;
  }
}
