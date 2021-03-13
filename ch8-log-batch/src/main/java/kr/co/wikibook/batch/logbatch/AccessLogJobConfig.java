package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
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

  @Bean
  public Job accessLogJob(JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory,
      DataSource dataSource) throws Exception {

    return jobBuilderFactory
        .get(JOB_NAME)
        .start(this.buildImportLogStep(stepBuilderFactory, dataSource))
        .next(this.buildExportSummaryStep(stepBuilderFactory, dataSource))
        .build();
  }

  TaskletStep buildExportSummaryStep(StepBuilderFactory stepBuilderFactory, DataSource dataSource)
      throws Exception {
    var userAccessOutput = new FileSystemResource("user-access-summary.csv");

    JdbcCursorItemReader<UserAccessSummary> dbReader = this
        .buildUserAccessSummaryReader(dataSource);
    dbReader.afterPropertiesSet();

    FlatFileItemWriter<UserAccessSummary> csvWriter = this.buildUserAccessSummaryWriter(userAccessOutput);
    csvWriter.afterPropertiesSet();

    var noTransaction = new DefaultTransactionAttribute(Propagation.NOT_SUPPORTED.value());

    return stepBuilderFactory.get("userAccessSummaryDbToCsv")
        .<UserAccessSummary, UserAccessSummary>chunk(300)
        .reader(dbReader)
        .writer(csvWriter)
        .transactionAttribute(noTransaction)
        .build();
  }

  TaskletStep buildImportLogStep(StepBuilderFactory stepBuilderFactory, DataSource dataSource) {

    ItemStreamReader<AccessLog> csvReader = this.accessLogReader(INJECTED_RESOURCED);
    JdbcBatchItemWriter<AccessLog> dbWriter = this.buildAccessLogWriter(dataSource);
    dbWriter.afterPropertiesSet();

    return stepBuilderFactory.get("accessLogCsvToDb")
        .<AccessLog, AccessLog>chunk(300)
        .reader(csvReader)
        .processor(new AccessLogProcessor())
        .writer(dbWriter)
        .build();
  }

  @Bean
  @JobScope
  public FlatFileItemReader<AccessLog> accessLogReader(
      @Value("#{jobParameters['accessLog']}") Resource resource) {

    return new FlatFileItemReaderBuilder<AccessLog>()
        .name("accessLogCsvReader")
        .resource(resource)
        .lineTokenizer(new DelimitedLineTokenizer())
        .fieldSetMapper(new AccessLogFieldSetMapper())
        .build();
  }

  FlatFileItemWriter<UserAccessSummary> buildUserAccessSummaryWriter(Resource resource) {
    return new FlatFileItemWriterBuilder<UserAccessSummary>()
        .name("userAccessSummaryCsvWriter")
        .resource(resource)
        .delimited()
        .delimiter(",")
        .fieldExtractor(new UserAccessSummaryFieldSetExtractor())
        .build();
  }

  JdbcCursorItemReader<UserAccessSummary> buildUserAccessSummaryReader(DataSource dataSource) {
    return new JdbcCursorItemReaderBuilder<UserAccessSummary>()
        .name("userAccessSummaryDbReader")
        .dataSource(dataSource)
        .sql(AccessLogSqls.COUNT_GROUP_BY_USERNAME)
        .rowMapper((resultSet, index) ->
            new UserAccessSummary(
                resultSet.getString("username"),
                resultSet.getInt("access_count")
            ))
        .build();
  }

  JdbcBatchItemWriter<AccessLog> buildAccessLogWriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<AccessLog>()
        .dataSource(dataSource)
        .sql(AccessLogSqls.INSERT)
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .build();
  }
}
