package kr.co.wikibook.batch.logbatch.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

class EmailReporterTest {
  private final Logger logger = LoggerFactory.getLogger(EmailJobReporter.class);

  @Test
  void skipSendEmail() {
    // given
    JobInstance jobInstance = new JobInstance(0L, "testJob");
    JobExecution jobExec = new JobExecution(0L);
    jobExec.setJobInstance(jobInstance);
    jobExec.setExitStatus(ExitStatus.COMPLETED);
    JavaMailSender mailSender = mock(JavaMailSender.class);
    EmailJobReporter sut = new EmailJobReporter(mailSender, List.of(), true);

    // when
    sut.afterJob(jobExec);

    // then
    verify(mailSender, never()).send(Mockito.any(SimpleMailMessage.class));
  }

  @Test
  void sendEmail() throws IOException {
    try (SimpleSmtpServer smtpServer = SimpleSmtpServer.start(SimpleSmtpServer.AUTO_SMTP_PORT)) {
      // given
      JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
      mailSender.setHost("localhost");
      mailSender.setPort(smtpServer.getPort());

      JobInstance jobInstance = new JobInstance(0L, "testJob");

      JobExecution jobExec = new JobExecution(0L);
      jobExec.setJobInstance(jobInstance);
      Instant startTime = Instant.parse("2020-02-02T16:02:00Z");
      Instant endTime = Instant.parse("2020-02-02T18:08:45Z");
      jobExec.setExitStatus(ExitStatus.COMPLETED);
      jobExec.setStartTime(Date.from(startTime));
      jobExec.setEndTime(Date.from(endTime));

      StepExecution stepExec = new StepExecution("testStep", jobExec, 0L);
      stepExec.setStartTime(Date.from(startTime));
      stepExec.setEndTime(Date.from(endTime));
      jobExec.addStepExecutions(List.of(stepExec));

      EmailJobReporter reporter = new EmailJobReporter(
          mailSender,
          List.of("sanghyuk.jung@navercorp.com"),
          false
      );

      // when
      reporter.afterJob(jobExec);

      // then
      List<SmtpMessage> emails = smtpServer.getReceivedEmails();
      assertThat(emails).hasSize(1);
      SmtpMessage email = emails.get(0);
      assertThat(email.getHeaderValue("Subject")).isEqualTo("testJob : COMPLETED (2:06:45)");
      assertThat(email.getHeaderValue("To")).isEqualTo("sanghyuk.jung@navercorp.com");
      logger.debug("email body : \n {}", email.getBody());
    }
  }
}