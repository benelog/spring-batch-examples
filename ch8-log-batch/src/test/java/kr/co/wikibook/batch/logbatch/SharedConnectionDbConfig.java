package kr.co.wikibook.batch.logbatch;

import javax.sql.DataSource;
import org.springframework.batch.item.database.ExtendedConnectionDataSourceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

public class SharedConnectionDbConfig {

  @Bean
  public DataSource dataSource() {
    var originalDataSource = new EmbeddedDatabaseBuilder()
        .setName("log-test-db2")
        .setType(EmbeddedDatabaseType.H2)
        .addScript("schema.sql")
        .build();
    return new ExtendedConnectionDataSourceProxy(originalDataSource);
  }

  @Bean
  public PlatformTransactionManager transactionManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }
}
