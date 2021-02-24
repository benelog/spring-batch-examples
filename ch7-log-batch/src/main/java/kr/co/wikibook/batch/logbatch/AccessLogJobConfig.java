package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldExtractor;
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
  public Job accessLogJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
      DataSource dataSource) {

    ItemStreamReader<AccessLog> csvReader = this.accessLogCsvReader(INJECTED_RESOURCED);
    var userAccessOutput = new FileSystemResource("user-access-summary.csv");
    var noTransaction = new DefaultTransactionAttribute(Propagation.NOT_SUPPORTED.value());
    return jobBuilderFactory
        .get(JOB_NAME)
        .start(stepBuilderFactory.get("accessLogCsvToDb")
            .<AccessLog, AccessLog>chunk(300)
            .reader(csvReader)
            .processor(new AccessLogProcessor())
            .writer(new AccessLogDbWriter(dataSource))
            .build())
        .next(stepBuilderFactory.get("userAccessSummaryDbToCsv")
            .<UserAccessSummary, UserAccessSummary>chunk(300)
            .reader(new UserAccessSummaryDbReader(dataSource))
            .writer(buildCsvWriter(userAccessOutput))
            .transactionAttribute(noTransaction)
            .build())
        .build();
  }

  @Bean
  @JobScope
  public FlatFileItemReader<AccessLog> accessLogCsvReader(
      @Value("#{jobParameters['accessLog']}") Resource resource) {

//    var reader = new FlatFileItemReader<AccessLog>();
//    reader.setResource(resource);
//    reader.setLineMapper(new AccessLogLineMapper());
//    return reader;
    return new FlatFileItemReaderBuilder<AccessLog>()
        .name("accessLogCsvReader")
        .resource(resource)
        .lineMapper(buildAccessLogLineMapper())
        .build();
  }

  FlatFileItemWriter<UserAccessSummary> buildCsvWriter(Resource resource) {
    return new FlatFileItemWriterBuilder<UserAccessSummary>()
        .name("userAccessSummaryCsvWriter")
        .resource(resource)
        .delimited()
        .fieldExtractor(buildUserAccessSummaryFieldSetExtractor())
        .build();
  }

  LineMapper<AccessLog> buildAccessLogLineMapper() {
    var lineMapper = new DefaultLineMapper<AccessLog>();
    lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
    lineMapper.setFieldSetMapper(new AccessLogFieldSetMapper());
    return lineMapper;
  }

  FieldExtractor<UserAccessSummary> buildUserAccessSummaryFieldSetExtractor() {
    BeanWrapperFieldExtractor<UserAccessSummary> fieldExtractor = new BeanWrapperFieldExtractor<>();
    fieldExtractor.setNames(new String[]{"username", "accessCount"});
    return fieldExtractor;
  }
}
