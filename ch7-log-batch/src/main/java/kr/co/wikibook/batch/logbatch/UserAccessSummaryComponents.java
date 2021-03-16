package kr.co.wikibook.batch.logbatch;

import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.core.io.Resource;

public class UserAccessSummaryComponents {

  public static FlatFileItemWriter<UserAccessSummary> buildCsvWriter(Resource resource) {
    var writer =  new FlatFileItemWriterBuilder<UserAccessSummary>()
        .name("userAccessSummaryCsvWriter")
        .resource(resource)
        .delimited()
        .delimiter(",")
        .fieldExtractor(new UserAccessSummaryFieldSetExtractor())
        .build();
    return Configs.afterPropertiesSet(writer);
  }
}
