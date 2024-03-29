package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
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
  public static final Resource INJECTED = null;

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final DataSource dataSource;

  public AccessLogJobConfig(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory,
      DataSource dataSource
  ) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.dataSource = dataSource;
  }

  @Bean
  public Job accessLogJob() {
    var userAccessOutput = new FileSystemResource("user-access-summary.csv");
    var noTransaction = new DefaultTransactionAttribute(Propagation.NOT_SUPPORTED.value());
    return this.jobBuilderFactory
        .get(JOB_NAME)
        .start(this.buildCsvToDbStep())
        .next(this.buildDbToCsvStep(userAccessOutput, noTransaction))
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<AccessLog> accessLogCsvReader(
      @Value("#{jobParameters['accessLog']}") Resource resource) {

    return new FlatFileItemReaderBuilder<AccessLog>()
        .name("accessLogCsvReader")
        .resource(resource)
        .lineTokenizer(new DelimitedLineTokenizer())
        .fieldSetMapper(new AccessLogFieldSetMapper())
        .build();
  }


  private TaskletStep buildCsvToDbStep() {
    ItemStreamReader<AccessLog> csvReader = this.accessLogCsvReader(INJECTED);

    return stepBuilderFactory.get("accessLogCsvToDb")
        .<AccessLog, AccessLog>chunk(300)
        .reader(csvReader)
        .processor(new AccessLogProcessor())
        .writer(new AccessLogDbWriter(dataSource))
        .build();
  }

  private TaskletStep buildDbToCsvStep(FileSystemResource userAccessOutput,
      DefaultTransactionAttribute noTransaction) {
    return stepBuilderFactory.get("userAccessSummaryDbToCsv")
        .<UserAccessSummary, UserAccessSummary>chunk(300)
        .reader(new UserAccessSummaryDbReader(dataSource))
        .writer(UserAccessSummaryComponents.buildCsvWriter(userAccessOutput))
        .transactionAttribute(noTransaction)
        .build();
  }
}
