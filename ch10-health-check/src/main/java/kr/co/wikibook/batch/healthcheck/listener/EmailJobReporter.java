package kr.co.wikibook.batch.healthcheck.listener;

import java.util.List;
import kr.co.wikibook.batch.healthcheck.util.Times;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailJobReporter extends JobExecutionListenerSupport {

  private static final String MAIL_SENDER = "benelog@gmail.com";
  private final Logger logger = LoggerFactory.getLogger(EmailJobReporter.class);

  private final JavaMailSender mailSender;
  private final List<String> receivers;
  private final boolean skipOnSuccess;

  public EmailJobReporter(JavaMailSender mailSender, List<String> receivers,
      boolean skipOnSuccess) {
    this.mailSender = mailSender;
    this.receivers = receivers;
    this.skipOnSuccess = skipOnSuccess;
  }

  @Override
  public void afterJob(JobExecution jobExec) {
    if (skipOnSuccess && jobExec.getStatus() == BatchStatus.COMPLETED) {
      logger.info("Skipped email report : {}", jobExec.getJobInstance().getJobName());
      return;
    }

    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(MAIL_SENDER);
    message.setTo(receivers.toArray(new String[receivers.size()]));
    message.setSubject(toSubject(jobExec));
    message.setText(toText(jobExec));
    mailSender.send(message);
  }

  private String toSubject(JobExecution jobExec) {
    String jobDuration = Times.getReadableDuration(
        jobExec.getStartTime().toInstant(),
        jobExec.getEndTime().toInstant()
    );
    String jobName = jobExec.getJobInstance().getJobName();
    return String.format(
        "%s : %s (%s)",
        jobName, jobExec.getStatus(), jobDuration
    );
  }

  private String toText(JobExecution jobExec) {
    StringBuilder text = new StringBuilder();
    for (StepExecution stepExec : jobExec.getStepExecutions()) {
      String stepDuration = Times.getReadableDuration(
          stepExec.getStartTime().toInstant(),
          stepExec.getEndTime().toInstant()
      );
      text.append(String.format("stepName: %s (%s)\n ", stepExec.getStepName(), stepDuration));
      text.append("Exceptions : " + stepExec.getFailureExceptions() + "\n");
      text.append("-------------\n");
    }
    return text.toString();
  }
}
