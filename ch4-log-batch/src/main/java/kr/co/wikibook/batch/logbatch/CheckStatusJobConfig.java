package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CheckStatusJobConfig {

  @Bean
  public Job checkStatusJob(
      JobBuilderFactory jobFactory, StepBuilderFactory stepFactory,
      DataSource dataSource
  ) {
    return jobFactory.get("checkStatusJob")
        .start(stepFactory.get("helloStep")
            .tasklet(new HelloTask())
            .build()
        ).next(stepFactory.get("checkDiskSpaceStep")
            .tasklet(new CheckDiskSpaceTask())
            .build()
        ).next(stepFactory.get("countAccessLogStep")
            .tasklet(new CountAccessLogTask(dataSource))
            .build()
        )
        .build();
  }
}
