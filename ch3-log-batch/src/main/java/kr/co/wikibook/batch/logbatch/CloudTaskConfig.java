package kr.co.wikibook.batch.logbatch;

import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("task-recording")
@EnableTask
@Configuration
public class CloudTaskConfig {

}
