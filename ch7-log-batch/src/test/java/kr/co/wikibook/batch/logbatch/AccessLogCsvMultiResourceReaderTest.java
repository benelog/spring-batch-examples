package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

class AccessLogCsvMultiResourceReaderTest {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Test
  void read() throws Exception {
    var resourcePatternResolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = resourcePatternResolver.getResources("classpath:/multi/*.csv");

    MultiResourceItemReader<AccessLog> reader = new MultiResourceItemReaderBuilder<AccessLog>()
        .name("accessLogMultiFileReader")
        .resources(resources)
        .delegate(constructDelegator())
        .build();

    reader.open(new ExecutionContext());

    int itemCount = 0;
    AccessLog item;
    while ((item = reader.read()) != null) {
      itemCount++;
      logger.debug("{}", item);
    }
    reader.close();
    assertThat(itemCount).isEqualTo(6);
  }

  private FlatFileItemReader<AccessLog> constructDelegator() {
    return new FlatFileItemReaderBuilder<AccessLog>()
        .name("accessLogCsvReader")
        .lineMapper(constructAccessLogLineMapper())
        .build();
  }

  private LineMapper<AccessLog> constructAccessLogLineMapper() {
    var lineMapper = new DefaultLineMapper<AccessLog>();
    lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
    lineMapper.setFieldSetMapper(new AccessLogFieldSetMapper());
    return lineMapper;
  }
}
