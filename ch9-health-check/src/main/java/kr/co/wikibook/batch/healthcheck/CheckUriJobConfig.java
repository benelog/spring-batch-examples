package kr.co.wikibook.batch.healthcheck;

import java.time.Duration;
import kr.co.wikibook.batch.healthcheck.util.Configs;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;


// java -Djob.name=checkUriJob -jar build/libs/health-check-0.0.1-SNAPSHOT.jar uriListFile=file:uri-1.txt
@Configuration
public class CheckUriJobConfig {
  public static final String INPUT_FILE_PARAM = "uriListFile";
  public static final String OUTPUT_FILE_PATH = "./status.csv";
  private static final String INPUT_FILE_PARAM_EXP = "#{jobParameters['" + INPUT_FILE_PARAM + "']}";

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();

  public CheckUriJobConfig(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
  }

  @Bean
  public Job checkUriJob() {
    var validator = new DefaultJobParametersValidator();
    validator.setRequiredKeys(new String[]{INPUT_FILE_PARAM});
    return this.jobBuilderFactory.get("checkUriJob")
//        .preventRestart()
        .validator(validator)
        .start(logResourceMetaStep())
        .next(checkUriStep())
        .build();
  }

  @Bean
  public Step logResourceMetaStep() {
    return stepBuilderFactory.get("logResourceMetaStep")
//        .allowStartIfComplete(true)
//        .startLimit(5)
        .transactionManager(transactionManager)
        .tasklet(logResourceMetaTasket(null))
        .build();
  }

  @Bean
  public Step checkUriStep() {
    return stepBuilderFactory.get("checkUriStep")
        .transactionManager(transactionManager)
        .<String, ResponseStatus>chunk(2)
        .reader(uriFileReader(null))
        .processor(callUriProcessor(null))
        .writer(buildResponseStatusFileWriter())
        .build();
  }

  @Bean
  @JobScope
  public Tasklet logResourceMetaTasket(
      @Value(INPUT_FILE_PARAM_EXP) Resource uriListFile) {
    var tasklet = new CallableTaskletAdapter();
    ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
    tasklet.setCallable(new LogResourceMetaTask(uriListFile, loggerFactory));
    return tasklet;
  }

  @Bean
  @JobScope
  public FlatFileItemReader<String> uriFileReader(
      @Value(INPUT_FILE_PARAM_EXP) Resource uriListFile) {
    return new FlatFileItemReaderBuilder<String>()
        .name("uriFileReader")
        .resource(uriListFile)
        .lineMapper(new PassThroughLineMapper())
        .build();
  }

  @Bean
  public CallUriProcessor callUriProcessor(@Value("${request.timeout}") Duration requestTimeout) {
    return new CallUriProcessor(requestTimeout);
  }

  private FlatFileItemWriter<ResponseStatus> buildResponseStatusFileWriter() {
    var outputFile = new FileSystemResource(OUTPUT_FILE_PATH);
    var writer =  new FlatFileItemWriterBuilder<ResponseStatus>()
        .name("responseStatusFileWriter")
        .resource(outputFile)
        .delimited()
        .fieldExtractor(item -> new Object[]{
            item.getStatusCode(), item.getResponseTime() + "ms", item.getUri()
        })
        .build();
    return Configs.afterPropertiesSet(writer);
  }
}
