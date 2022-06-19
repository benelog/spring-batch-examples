package kr.co.wikibook.batch.healthcheck.backup;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.Callable;
import kr.co.wikibook.batch.healthcheck.support.Transactions;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BackupDailyJobConfig {

  private JobBuilderFactory jobBuilderFactory;
  private StepBuilderFactory stepBuilderFactory;

  private static final String PARAM_SOURCE_DIR = "#{jobParameters['sourceDirectory']}";
  private static final String PARAM_TARGET_PARENT_DIR = "#{jobParameters['targetParentDirectory']}";

  public BackupDailyJobConfig(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job backupDailyJob() {
    Step checkDiskSpaceStep = checkDiskSpaceStep(null, null);
    BackupFlowDecider decider = new BackupFlowDecider();

    return this.jobBuilderFactory.get("backupDailyJob")
        .start(checkDiskSpaceStep)
        .next(decider)
        .on("COMPLETED")
        .to(backupDailyStep(null))

        .from(decider)
        .on("RETRY")
        .to(deleteOldDirectoriesStep(null))
        .next(checkDiskSpaceStep)

        .from(decider)
        .on("FAILED")
        .fail()
        .end()
        .build();
  }

  @Bean
  @JobScope
  public ParameterDirectories parameterDirectories(
      @Value(PARAM_SOURCE_DIR) String sourceDir,
      @Value(PARAM_TARGET_PARENT_DIR) String targetParentDir
  ) {
    return new ParameterDirectories(sourceDir, targetParentDir);
  }

  @Bean
  @JobScope
  public TaskletStep checkDiskSpaceStep(
      ParameterDirectories paramDirs,
      @Value("#{jobExecutionContext}") Map<String, Object> contextMap
  ) {
    var tasklet = new CheckDiskSpaceTasklet(
        paramDirs.getSourceDirectory(),
        paramDirs.getTargetParentDirectory()
    );

    return stepBuilderFactory.get("checkDiskSpaceStep")
        .allowStartIfComplete(true)
        .tasklet(tasklet)
        .transactionAttribute(Transactions.TX_NOT_SUPPORTED)
        .build();
  }

  @Bean
  @JobScope
  public TaskletStep deleteOldDirectoriesStep(ParameterDirectories paramDirs) {

    var task = new DeleteOldDirectoriesTask(
        paramDirs.getTargetParentDirectory(), 10, Clock.systemUTC()
    );

    return buildStep("deleteOldDirectoriesStep", task);
  }

  @Bean
  @JobScope
  public TaskletStep backupDailyStep(ParameterDirectories paramDirs) {
    var task = new BackupDailyTask(
        paramDirs.getSourceDirectory(),
        paramDirs.getTargetParentDirectory(),
        Clock.systemUTC()
    );
    return buildStep("backupDailyStep", task);
  }


  private TaskletStep buildStep(String stepName, Callable<RepeatStatus> task) {
    var tasklet = new CallableTaskletAdapter();
    tasklet.setCallable(task);
    return stepBuilderFactory.get(stepName)
        .tasklet(tasklet)
        .transactionAttribute(Transactions.TX_NOT_SUPPORTED)
        .build();
  }
}
