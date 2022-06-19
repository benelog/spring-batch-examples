package kr.co.wikibook.batch.healthcheck.backup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;

class CheckDiskSpaceTaskletTest {
  @TempDir
  Path tempPath;

  @Test
  void invalidSourceDirectory() {
    Path nonExistentPath = Path.of("noex");
    assertThatThrownBy(
        () -> new CheckDiskSpaceTasklet(
            nonExistentPath.toFile(),
            this.tempPath.toFile()
        )
    ).isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("sourceDirectory가 없는 경로입니다.");
  }

  @Test
  void invalidTargetParentDirectory() {
    Path nonExistentPath = Path.of("noex");
    assertThatThrownBy(
        () -> new CheckDiskSpaceTasklet(
            this.tempPath.toFile(),
            nonExistentPath.toFile()
        )
    ).isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("targetParentDirectory가 없는 경로입니다.");
  }

  @Test
  void checkDiskSpace() throws IOException {
    // given
    Path sourceFile = this.tempPath.resolve("test.txt");
    Files.writeString(sourceFile, "test content");

    File targetParentDir = mock(File.class);
    given(targetParentDir.exists()).willReturn(true);
    given(targetParentDir.isDirectory()).willReturn(true);
    given(targetParentDir.getUsableSpace()).willReturn(1024L);

    var tasklet = new CheckDiskSpaceTasklet(this.tempPath.toFile(), targetParentDir);

    JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(
        jobExecution, "testStep", 1L
    );
    var stepContribution = new StepContribution(stepExecution);
    var chunkContext = new ChunkContext(new StepContext(stepExecution));

    // when
    RepeatStatus status = tasklet.execute(stepContribution, chunkContext);

    // then
    assertThat(status).isEqualTo(RepeatStatus.FINISHED);
    ExecutionContext executionContext = jobExecution.getExecutionContext();
    long usableSpace = executionContext.getLong("usableSpace");
    assertThat(usableSpace).isEqualTo(1024L);
    long sourceSize = executionContext.getLong("sourceSize");
    assertThat(sourceSize).isEqualTo(12L);
    long executionCount = executionContext.getInt("executionCount");
    assertThat(executionCount).isEqualTo(1);

    tasklet.execute(stepContribution, chunkContext); // 2번째 실행
    executionCount = executionContext.getInt("executionCount");
    assertThat(executionCount).isEqualTo(2);
  }
}
