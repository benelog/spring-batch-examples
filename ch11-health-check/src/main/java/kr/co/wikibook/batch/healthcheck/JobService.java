package kr.co.wikibook.batch.healthcheck;

import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource
public class JobService {
  private final JobOperator operator;

  public JobService(JobOperator operator) {
    this.operator = operator;
  }

  @ManagedOperation
  public void stopJobExecution(long jobExeuctionId)
      throws NoSuchJobExecutionException, JobExecutionNotRunningException {
    operator.stop(jobExeuctionId);
  }
}
