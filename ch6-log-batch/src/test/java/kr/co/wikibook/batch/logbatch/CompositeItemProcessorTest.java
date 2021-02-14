package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;

class CompositeItemProcessorTest {
  @Test
  void compositeProcess() throws Exception {
    var compositeProcessor = new CompositeItemProcessor<Integer, Integer>();
    compositeProcessor.setDelegates(List.of(plus2Processor(), multiply10Processor()));
    compositeProcessor.afterPropertiesSet();

    Integer processed = compositeProcessor.process(1);
    assertThat(processed).isEqualTo(30);
  }

  ItemProcessor<Integer, Integer> plus2Processor() {
    return (item) -> item + 2;
  }

  ItemProcessor<Integer, Integer> multiply10Processor() {
    return (item) -> item * 10;
  }
}
