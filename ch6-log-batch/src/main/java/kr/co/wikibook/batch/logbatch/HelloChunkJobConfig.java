package kr.co.wikibook.batch.logbatch;

import java.util.PrimitiveIterator.OfInt;
import java.util.stream.IntStream;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.resource.StepExecutionSimpleCompletionPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Configuration
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = HelloChunkJobConfig.JOB_NAME)
public class HelloChunkJobConfig {

  public static final String JOB_NAME = "helloChunkJob";

  @Bean
  public Job helloChunkJob(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory) {

    var noTransaction = new DefaultTransactionAttribute(Propagation.NOT_SUPPORTED.value());

    var completionPolicy = new StepExecutionSimpleCompletionPolicy();
    completionPolicy.setKeyName("chunkSize");

    return jobBuilderFactory.get(JOB_NAME)
        .start(stepBuilderFactory.get("printSequence")
//            .<Integer, Integer>chunk(3)
            .<Integer, Integer>chunk(completionPolicy)
            .reader(sequenceReader(1, 10))
            .processor(plus10Processor())
            .writer(consoleWriter())
            .stream(new HelloItemStream())
            .transactionAttribute(noTransaction)
            .listener(completionPolicy)
            .build())
        .build();
  }

  ItemReader<Integer> sequenceReader(int from, int to) {
    IntStream itemRange = IntStream.range(from, to + 1);
    OfInt iterator = itemRange.iterator();
    return new IteratorItemReader<>(iterator);
  }

  ItemProcessor<Integer, Integer> plus10Processor() {
    return (item) -> item + 10;
  }

  ItemWriter<Integer> consoleWriter() {
    return (items) -> System.out.println(items);
  }

}
