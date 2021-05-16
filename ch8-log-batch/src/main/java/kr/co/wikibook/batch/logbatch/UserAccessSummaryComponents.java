package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import kr.co.wikibook.batch.logbatch.util.Configs;
import org.springframework.batch.item.database.ExtendedConnectionDataSourceProxy;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.DataClassRowMapper;

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

  public static JdbcCursorItemReader<UserAccessSummary> buildDbReader(DataSource dataSource, boolean sharedConection) {
    var reader =  new JdbcCursorItemReaderBuilder<UserAccessSummary>()
        .name("userAccessSummaryDbReader")
        .dataSource(dataSource)
        .useSharedExtendedConnection(sharedConection)
        .sql(AccessLogSql.COUNT_GROUP_BY_USERNAME)
        .rowMapper(new DataClassRowMapper<>(UserAccessSummary.class))
        .build();
    return Configs.afterPropertiesSet(reader);
  }
}
