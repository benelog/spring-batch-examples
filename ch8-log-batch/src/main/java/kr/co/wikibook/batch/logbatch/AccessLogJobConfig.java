package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Configuration
public class AccessLogJobConfig {

  public static final String JOB_NAME = "accessLogJob";
  public static final Resource INJECTED = null;

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final DataSource dataSource;

  public AccessLogJobConfig(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory,
      @Qualifier("mainDataSource")
      DataSource dataSource
  ) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.dataSource = dataSource;
  }

  @Bean
  public Job accessLogJob() throws Exception {
    return this.jobBuilderFactory
        .get(JOB_NAME)
        .start(this.buildCsvToDbStep())
        .next(this.buildDbToCsvStep())
        .build();
  }

  private TaskletStep buildDbToCsvStep() {
    JdbcCursorItemReader<UserAccessSummary> dbReader = UserAccessSummaryComponents.buildDbReader(this.dataSource);

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

  private TaskletStep buildCsvToDbStep() {
    ItemStreamReader<AccessLog> csvReader = this.accessLogReader(INJECTED);
    JdbcBatchItemWriter<AccessLog> dbWriter = AccessLogComponents.buildAccessLogWriter(this.dataSource);
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
