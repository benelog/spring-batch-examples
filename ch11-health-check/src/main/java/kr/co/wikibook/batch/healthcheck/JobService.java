package kr.co.wikibook.batch.healthcheck;

import java.util.Set;

import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
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
  public void stopJobExecution(long jobExecutionId)
      throws NoSuchJobExecutionException, JobExecutionNotRunningException {
    this.operator.stop(jobExecutionId);
  }

  @ManagedOperation
  public void stopAllJobs()
      throws NoSuchJobExecutionException, JobExecutionNotRunningException, NoSuchJobException {
    Set<String> jobNames = this.operator.getJobNames();
    for (String jobName : jobNames) {
      Set<Long> runningExecutions = operator.getRunningExecutions(jobName);
      for (Long execId : runningExecutions) {
        operator.stop(execId);
      }
    }
  }
}
