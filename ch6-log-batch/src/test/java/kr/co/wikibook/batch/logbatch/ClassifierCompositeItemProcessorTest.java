package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.batch.item.support.PassThroughItemProcessor;

class ClassifierCompositeItemProcessorTest {

  @Test
  public void oddEvenProcessor() throws Exception {
    var compositeProcessor = new ClassifierCompositeItemProcessor<Integer, Integer>();
    compositeProcessor.setClassifier(new OddEvenClassifier<>(
        (Integer item) -> item * 2,
        new PassThroughItemProcessor<>()
    ));
    assertThat(compositeProcessor.process(3)).isEqualTo(6);
    assertThat(compositeProcessor.process(4)).isEqualTo(4);
  }
}
