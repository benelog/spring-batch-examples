package kr.co.wikibook.batch.healthcheck.metadata;

import java.util.function.Supplier;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

public class RemoveOldJobMetadataJobFactory {
  private final StepBuilderFactory stepBuilderFactory;
  private final JobBuilderFactory jobBuilderFactory;
  private final DataSource dataSource;
  private final String tablePrefix;

  public RemoveOldJobMetadataJobFactory(
      JobRepository jobRepository,
      DataSource dataSource,
      String tablePrefix) {
    this.jobBuilderFactory = new JobBuilderFactory(jobRepository);
    var transactionManager = new DataSourceTransactionManager(dataSource);
    this.stepBuilderFactory = new StepBuilderFactory(jobRepository, transactionManager);
    this.dataSource = dataSource;
    this.tablePrefix = tablePrefix;
  }

  public Job createJob(String jobName) {
    var sqlProvider = new MetadataSqlProvider(this.tablePrefix);
    Step selectKeyTasklet = this.buildSelectKeyTasklet(tablePrefix);
    return jobBuilderFactory.get(jobName)
        .incrementer(new RunIdIncrementer())
        .start(selectKeyTasklet)
        .on("EMPTY").end()// 지울 로그가 없으면 바로 종료
        .from(selectKeyTasklet)
        .next(
            buildJdbcStep("deleteStepExecutionContext", sqlProvider::deleteStepExecutionContext)
        ).next(
            buildJdbcStep("deleteStepExecution", sqlProvider::deleteStepExecution)
        ).next(
            buildJdbcStep("deleteJobExecutionContext", sqlProvider::deleteJobExecutionContext)
        ).next(
            buildJdbcStep("deleteJobExecutionParams", sqlProvider::deleteJobExecutionParams)
        ).next(
            buildJdbcStep("deleteJobExecution", sqlProvider::deleteJobExecution)
        ).next(
            buildJdbcStep("deleteJobInstance", sqlProvider::deleteJobInstance)
        ).end()
        .build();
  }

  private Step buildSelectKeyTasklet(String tablePrefix) {
    var dao = new JobMetadataDao(this.dataSource, tablePrefix);
    var tasklet = new SelectJobMetadataKeyTasklet(dao);
    var noTransaction = new DefaultTransactionAttribute(Propagation.NOT_SUPPORTED.value());
    return stepBuilderFactory.get("selectJobMetadataKey")
        .tasklet(tasklet)
        .transactionAttribute(noTransaction)
        .listener(tasklet)
        .build();
  }

  private TaskletStep buildJdbcStep(String stepName, Supplier<String> sqlSupplier) {
    return stepBuilderFactory.get(stepName)
        .tasklet(new JdbcUpdateTasklet(this.dataSource, sqlSupplier.get()))
        .build();
  }
}
