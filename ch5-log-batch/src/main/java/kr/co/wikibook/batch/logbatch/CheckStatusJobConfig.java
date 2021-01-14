package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = CheckStatusJobConfig.JOB_NAME)
public class CheckStatusJobConfig {

  public static final String JOB_NAME = "checkStatusJob";

  @Bean
  public Job checkStatusJob(
      JobBuilderFactory jobFactory, StepBuilderFactory stepFactory,
      DataSource dataSource
  ) {
    return jobFactory.get(JOB_NAME)
        .start(stepFactory.get("checkDiskSpaceStep")
            .tasklet(new CheckDiskSpaceTask())
            .build()
        ).next(stepFactory.get("countAccessLogStep")
            .tasklet(new CountAccessLogTask(dataSource))
            .build()
        ).next(stepFactory.get("accessExecutionContext")
        .tasklet(accessTasket())
        .build()
    )
        .build();
  }

  private Tasklet accessTasket() {
    var tasklet = new CallableTaskletAdapter();
    tasklet.setCallable(new AccessExecutionContextTask());
    return tasklet;
  }
}
