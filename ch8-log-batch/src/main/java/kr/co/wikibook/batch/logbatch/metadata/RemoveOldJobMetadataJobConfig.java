package kr.co.wikibook.batch.logbatch.metadata;

import java.util.function.Supplier;
import javax.sql.DataSource;
import kr.co.wikibook.batch.logbatch.AccessLogJobConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = RemoveOldJobMetadataJobConfig.JOB_NAME)
public class RemoveOldJobMetadataJobConfig {

  public static final String JOB_NAME = "removeOldJobMetadataJob";

  @Autowired
  private StepBuilderFactory stepFactory;

  @Autowired
  private JobBuilderFactory jobFactory;

  @Autowired
  @Qualifier("jobDataSource")
  private DataSource dataSource;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Bean
  public Job removeOldJobMetadataJob(BatchProperties batchProperties) {
    String tablePrefix = batchProperties.getTablePrefix();
    var sql = new JobMetadataSql(tablePrefix);
    return jobFactory.get(JOB_NAME)
        .incrementer(new RunIdIncrementer())
        .start(selectKeyTask(tablePrefix))
        .on("EMPTY").end()// 지울 로그가 없으면 바로 종료
        .from(selectKeyTask(tablePrefix))
        .next(
            buildJdbcStep("deleteStepExecutionContext", sql::deleteStepExecutionContext)
        ).next(
            buildJdbcStep("deleteStepExecution", sql::deleteStepExecution)
        ).next(
            buildJdbcStep("deleteJobExecutionContext", sql::deleteJobExecutionContext)
        ).next(
            buildJdbcStep("deleteJobExecutionParams", sql::deleteJobExecutionParams)
        ).next(
            buildJdbcStep("deleteJobExecution", sql::deleteJobExecution)
        ).next(
            buildJdbcStep("deleteJobInstance", sql::deleteJobInstance)
        ).end()
        .build();
  }

  public Step selectKeyTask(String tablePrefix) {
    var dao = new JobMetadataDao(this.dataSource, tablePrefix);
    var tasklet = new SelectJobMetadataKeyTasklet(dao);
    return stepFactory.get("selectJobMetadataKey")
        .tasklet(tasklet)
        .transactionManager(transactionManager)
        .listener(tasklet)
        .build();
  }

  private TaskletStep buildJdbcStep(String stepName, Supplier<String> sqlSupplier) {
    return stepFactory.get(stepName)
        .tasklet(new JdbcUpdateTasklet(this.dataSource, sqlSupplier.get()))
        .transactionManager(this.transactionManager)
        .build();
  }
}
