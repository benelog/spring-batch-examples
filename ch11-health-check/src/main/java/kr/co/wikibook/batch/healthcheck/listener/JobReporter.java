package kr.co.wikibook.batch.healthcheck.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

public class JobReporter extends JobExecutionListenerSupport {

  private final Logger logger = LoggerFactory.getLogger(JobReporter.class);

  @Override
  public void afterJob(JobExecution jobExec) {

    String jobName = jobExec.getJobInstance().getJobName();
    long duration = jobExec.getEndTime().getTime() - jobExec.getStartTime().getTime();
    logger.info("Job name: {}, duration : {} millisec", jobName, duration);
    for (StepExecution stepExec : jobExec.getStepExecutions()) {
      long stepDuration = stepExec.getEndTime().getTime() - stepExec.getStartTime().getTime();
      logger.info("Step name: {}, duration : {} millisec\n ", stepExec.getStepName(), stepDuration);
      logger.info(stepExec.getSummary());
      logger.info("Exceptions : {}", stepExec.getFailureExceptions());
    }
  }
}
