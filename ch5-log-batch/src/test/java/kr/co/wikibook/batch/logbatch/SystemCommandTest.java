package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.core.step.tasklet.SystemCommandException;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;

public class SystemCommandTest {

  SystemCommandTasklet systemCommandTasklet = new SystemCommandTasklet();
  StepContribution stepContribution = null;
  ChunkContext chunkContext = null;

  @BeforeEach
  void setUp() {
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
    stepContribution = new StepContribution(stepExecution);
    chunkContext = new ChunkContext(new StepContext(stepExecution));
  }

  @Test
  @EnabledOnOs({OS.LINUX, OS.MAC})
  void execute(@TempDir Path tempPath) throws Exception {
    // given
    String shellPath = readAbsolutePath("command.sh");
    systemCommandTasklet.setCommand(shellPath);
    systemCommandTasklet.setTimeout(1000);
    systemCommandTasklet.setEnvironmentParams(new String[]{"MESSAGE=Hello"});
    systemCommandTasklet.setWorkingDirectory(tempPath.toString());
    systemCommandTasklet.afterPropertiesSet();

    // when
    RepeatStatus taskStatus = systemCommandTasklet.execute(stepContribution, chunkContext);

    // then
    assertThat(stepContribution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    assertThat(taskStatus).isEqualTo(RepeatStatus.FINISHED);
    String content = Files.readString(tempPath.resolve("output.txt"));
    assertThat(content).isEqualTo("Hello\n");
  }

  @Test
  void executeTimeout() throws Exception {
    // given
    systemCommandTasklet.setCommand("sleep 3000");
    systemCommandTasklet.setTimeout(100);
    systemCommandTasklet.afterPropertiesSet();

    // when, then
    assertThatExceptionOfType(SystemCommandException.class)
        .isThrownBy(() -> systemCommandTasklet.execute(stepContribution, chunkContext))
        .withMessageContaining("Execution of system command did not finish within the timeout");
  }

  private String readAbsolutePath(String classPathFile) throws IOException {
    ClassPathResource resource = new ClassPathResource(classPathFile);
    return resource.getFile().getAbsolutePath();
  }
}
