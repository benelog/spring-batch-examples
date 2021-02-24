package kr.co.wikibook.batch.logbatch;

import java.util.PrimitiveIterator.OfInt;
import java.util.stream.IntStream;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.PassThroughFieldExtractor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = TransactionalFileJobConfig.JOB_NAME)
public class TransactionalFileJobConfig {

  public static final String JOB_NAME = "transactionalFileJob";

  @Bean
  public Job transactionalFileJob(JobBuilderFactory jobFactory, StepBuilderFactory stepFactory) {
    var numberOutput = new FileSystemResource("numbers.txt");

    var writer = new CompositeItemWriterBuilder<Integer>()
        .delegates(
            buildFlatFileItemWriter(numberOutput),
            buildIntentionalErrorWriter()
        ).build();

    return jobFactory
        .get(JOB_NAME)
        .start(stepFactory.get("generateSequenceFile")
            .transactionManager(new ResourcelessTransactionManager())
            .<Integer, Integer>chunk(10)
            .reader(buildSequenceReader(1, 30))
            .writer(writer)
            .build())
        .build();
  }

  ItemReader<Integer> buildSequenceReader(int from, int to) {
    IntStream itemRange = IntStream.range(from, to + 1);
    OfInt iterator = itemRange.iterator();
    return new IteratorItemReader<>(iterator);
  }

  ItemWriter<Integer> buildIntentionalErrorWriter() {
    return (numbers) -> {
      for (Integer number : numbers) {
        if (number == 13) {
          throw new IllegalStateException("의도적인 에러");
        }
        System.out.println(number);
      }
    };
  }

  FlatFileItemWriter<Integer> buildFlatFileItemWriter(Resource resource) {
    return new FlatFileItemWriterBuilder<Integer>()
        .name("userAccessSummaryCsvWriter")
        .resource(resource)
        .delimited()
        .fieldExtractor(new PassThroughFieldExtractor<>())
        .build();
  }
}
