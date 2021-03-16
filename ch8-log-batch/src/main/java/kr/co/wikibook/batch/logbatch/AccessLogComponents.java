package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;

public class AccessLogComponents {

  public static JdbcBatchItemWriter<AccessLog> buildAccessLogWriter(DataSource dataSource) {
    var writer =  new JdbcBatchItemWriterBuilder<AccessLog>()
        .dataSource(dataSource)
        .sql(AccessLogSqls.INSERT)
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .build();
    return Configs.afterPropertiesSet(writer);
  }
}
