package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.H2PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(TestDbConfig.class)
class UserAccessSummaryPagingDbReaderTest {

  @Test
  @Sql("classpath:/access-log.sql")
  void readItems(@Autowired DataSource dataSource) throws Exception {
    // given
    PagingQueryProvider queryProvider = buildPagingQueryProvider();
    JdbcPagingItemReader<UserAccessSummary> reader = buildPagingItemReader(
        dataSource,
        queryProvider
    );
    reader.afterPropertiesSet();

    // when
    UserAccessSummary item1 = reader.read();
    UserAccessSummary item2 = reader.read();
    UserAccessSummary item3 = reader.read();
    reader.close();

    // then
    assertThat(item1.getUsername()).isEqualTo("benelog");
    assertThat(item1.getAccessCount()).isEqualTo(2);
    assertThat(item2.getUsername()).isEqualTo("jojoldu");
    assertThat(item2.getAccessCount()).isEqualTo(1);
    assertThat(item3).isNull();
  }

  private JdbcPagingItemReader<UserAccessSummary> buildPagingItemReader(
      DataSource dataSource, PagingQueryProvider queryProvider) {
    JdbcPagingItemReader<UserAccessSummary> reader = new JdbcPagingItemReaderBuilder<UserAccessSummary>()
        .name("accessLogDbReader")
        .dataSource(dataSource)
        .queryProvider(queryProvider)
        .rowMapper(new DataClassRowMapper<>(UserAccessSummary.class))
        .pageSize(2)
        .build();
    return reader;
  }

  private PagingQueryProvider buildPagingQueryProvider() {
    var queryProvider = new H2PagingQueryProvider();
    queryProvider.setSelectClause("username, COUNT(1) AS access_count");
    queryProvider.setFromClause("FROM access_log");
    queryProvider.setGroupClause("GROUP BY username");
    queryProvider.setSortKeys(Map.of("username", Order.ASCENDING));
    return queryProvider;
  }
}
