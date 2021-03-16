package kr.co.wikibook.batch.logbatch;

import com.fasterxml.jackson.annotation.JsonProperty.Access;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = AccessLogJobConfig.JOB_NAME)
public class AccessLogJobConfig {

  public static final String JOB_NAME = "accessLogJob";
  public static final Resource INJECTED_RESOURCED = null;

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  public AccessLogJobConfig(JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job accessLogJob(DataSource dataSource) throws Exception {
    return this.jobBuilderFactory
        .get(JOB_NAME)
        .start(this.buildCsvToDbStep(dataSource))
        .next(this.buildDbToCsvStep(dataSource))
        .build();
  }

  TaskletStep buildDbToCsvStep(DataSource dataSource) throws Exception {
    JdbcCursorItemReader<UserAccessSummary> dbReader = UserAccessSummaryComponents.buildDbReader(dataSource);

    var userAccessOutput = new FileSystemResource("user-access-summary.csv");
    FlatFileItemWriter<UserAccessSummary> csvWriter = UserAccessSummaryComponents.buildCsvWriter(userAccessOutput);

    var noTransaction = new DefaultTransactionAttribute(Propagation.NOT_SUPPORTED.value());

    return this.stepBuilderFactory.get("userAccessSummaryDbToCsv")
        .<UserAccessSummary, UserAccessSummary>chunk(300)
        .reader(dbReader)
        .writer(csvWriter)
        .transactionAttribute(noTransaction)
        .build();
  }

  private TaskletStep buildCsvToDbStep(DataSource dataSource) {
    ItemStreamReader<AccessLog> csvReader = this.accessLogReader(INJECTED_RESOURCED);
    JdbcBatchItemWriter<AccessLog> dbWriter = AccessLogComponents.buildAccessLogWriter(dataSource);
    return this.stepBuilderFactory.get("accessLogCsvToDb")
        .<AccessLog, AccessLog>chunk(300)
        .reader(csvReader)
        .processor(new AccessLogProcessor())
        .writer(dbWriter)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<AccessLog> accessLogReader(
      @Value("#{jobParameters['accessLog']}") Resource resource) {

    return new FlatFileItemReaderBuilder<AccessLog>()
        .name("accessLogCsvReader")
        .resource(resource)
        .lineTokenizer(new DelimitedLineTokenizer())
        .fieldSetMapper(new AccessLogFieldSetMapper())
        .build();
  }
}
