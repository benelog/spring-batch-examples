package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
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
    return ConfigUtils.afterPropertiesSet(writer);
  }

  public static JdbcCursorItemReader<UserAccessSummary> buildDbReader(DataSource dataSource) {
    var reader =  new JdbcCursorItemReaderBuilder<UserAccessSummary>()
        .name("userAccessSummaryDbReader")
        .dataSource(dataSource)
        .sql(AccessLogSqls.COUNT_GROUP_BY_USERNAME)
        .rowMapper((resultSet, index) ->
            new UserAccessSummary(
                resultSet.getString("username"),
                resultSet.getInt("access_count")
            ))
        .build();
    return ConfigUtils.afterPropertiesSet(reader);
  }
}
