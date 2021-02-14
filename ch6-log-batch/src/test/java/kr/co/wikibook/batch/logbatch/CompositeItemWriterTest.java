package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.ListItemWriter;

class CompositeItemWriterTest {
  @Test
  void compositeWrite() throws Exception {
    // given
    var delegate1 = new ListItemWriter<Integer>();
    var delegate2 = new ListItemWriter<Integer>();
    var compositeWriter = new CompositeItemWriter<Integer>();
    compositeWriter.setDelegates(List.of(delegate1, delegate2));
    compositeWriter.afterPropertiesSet();
    List<Integer> items = List.of(1, 2, 3);

    // when
    compositeWriter.write(items);

    // then
    assertThat(delegate1.getWrittenItems()).isEqualTo(items);
    assertThat(delegate2.getWrittenItems()).isEqualTo(items);
  }
}
