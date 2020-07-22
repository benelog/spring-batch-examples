package kr.co.wikibook.batch.logbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloJobConfig {

  @Bean
  public Job helloJob(JobBuilderFactory jobFactory, StepBuilderFactory stepFactory) {
    Tasklet helloTask = new HelloTask();
    Step helloStep = stepFactory.get("helloStep")
        .tasklet(helloTask)
        .build();
    return jobFactory.get("helloJob")
        .start(helloStep)
        .build();
  }

//  더 간결하게 선언도 가능
//  @Bean
//  public Job helloJob(JobBuilderFactory jobFactory, StepBuilderFactory stepFactory) {
//    return jobFactory.get("helloJob")
//        .start(stepFactory.get("helloStep")
//            .tasklet(new HelloTask())
//            .build()
//        )
//        .build();
//  }
}
