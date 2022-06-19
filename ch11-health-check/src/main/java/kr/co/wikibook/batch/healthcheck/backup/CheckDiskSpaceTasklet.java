package kr.co.wikibook.batch.healthcheck.backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.util.Assert;

public class CheckDiskSpaceTasklet implements Tasklet {
  private final File sourceDirectory;
  private final File targetParentDirectory;

  public CheckDiskSpaceTasklet(File sourceDirectory, File targetParentDirectory) {
    Assert.isTrue(sourceDirectory.exists(), "sourceDirectory가 없는 경로입니다.");
    Assert.isTrue(sourceDirectory.isDirectory(), "sourceDirectory는 디렉토리이어야합니다.");
    this.sourceDirectory = sourceDirectory;

    Assert.isTrue(targetParentDirectory.exists(), "targetParentDirectory가 없는 경로입니다.");
    Assert.isTrue(targetParentDirectory.isDirectory(), "targetParentDirectory는 디렉토리이어야합니다.");
    this.targetParentDirectory = targetParentDirectory;
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws IOException {
    JobExecution jobExecution = contribution.getStepExecution().getJobExecution();
    ExecutionContext executionContext = jobExecution.getExecutionContext();
    long sourceSize = getSourceDirectorySize();

    executionContext.putLong("sourceSize", sourceSize);
    long usableSpace = this.targetParentDirectory.getUsableSpace();
    executionContext.putLong("usableSpace", usableSpace);

    int executionCount = executionContext.getInt("executionCount", 0);
    executionCount++;
    executionContext.putInt("executionCount", executionCount);
    return RepeatStatus.FINISHED;
  }

  private long getSourceDirectorySize() throws IOException {
    return Files.walk(this.sourceDirectory.toPath())
        .map(path -> path.toFile())
        .filter(file -> file.isFile())
        .mapToLong(file -> file.length())
        .sum();
  }
}
