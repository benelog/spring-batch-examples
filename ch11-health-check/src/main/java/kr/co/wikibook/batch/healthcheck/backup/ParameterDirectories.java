package kr.co.wikibook.batch.healthcheck.backup;

import java.io.File;

public class ParameterDirectories {
  private final File sourceDirectory;
  private final File targetParentDirectory;

  public ParameterDirectories(String sourceDir, String targetParentDir) {
    this.sourceDirectory = new File(sourceDir);
    this.targetParentDirectory = new File(targetParentDir);
  }

  public File getTargetParentDirectory() {
    return targetParentDirectory;
  }

  public File getSourceDirectory() {
    return sourceDirectory;
  }
}
