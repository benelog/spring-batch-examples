package kr.co.wikibook.batch.logbatch.listener;

import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    String jobName = jobExec.getJobInstance().getJobName();

    if (skipOnSuccess && jobExec.getExitStatus() == ExitStatus.COMPLETED) {
      logger.info("Skipped email report : {}", jobName);
      return;
    }

    String jobDuration = getReadableDuration(jobExec.getStartTime().toInstant(), jobExec.getEndTime().toInstant());
    String subject = String
        .format("%s : %s (%s)", jobName, jobExec.getExitStatus().getExitCode(), jobDuration);

    StringBuilder text = new StringBuilder();
    for (StepExecution stepExec : jobExec.getStepExecutions()) {
      String stepDuration = getReadableDuration(stepExec.getStartTime().toInstant(),
          stepExec.getEndTime().toInstant());
      text.append(String.format("stepName: %s (%s)\n ", stepExec.getStepName(), stepDuration));
      text.append("Exceptions : " + stepExec.getFailureExceptions() + "\n");
      text.append("-------------\n");
    }

    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(MAIL_SENDER);
    message.setTo(receivers.toArray(new String[receivers.size()]));
    message.setSubject(subject);
    message.setText(text.toString());
    mailSender.send(message);
  }

  // 2개의 Instant의 차이를 '시:분:초' 형식으로 반환
  public static String getReadableDuration(Instant from, Instant to) {
    long durationSeconds = to.getEpochSecond() - from.getEpochSecond();
    long hours = durationSeconds / 60 / 60;
    long leftSeconds = durationSeconds - hours * 60 * 60;
    long minuets = leftSeconds / 60;
    long seconds = leftSeconds - minuets * 60;
    return String.format("%d:%02d:%02d", hours, minuets, seconds);
  }
}

