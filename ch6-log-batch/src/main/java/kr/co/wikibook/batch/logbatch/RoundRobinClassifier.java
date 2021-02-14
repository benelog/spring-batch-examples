package kr.co.wikibook.batch.logbatch;

import java.util.List;
import org.springframework.classify.Classifier;

public class RoundRobinClassifier<C, T> implements Classifier<C, T> {

  private int index = -1;
  private final int rounds;
  private final List<T> targets;

  public RoundRobinClassifier(T... targets) {
    this.targets = List.of(targets);
    this.rounds = targets.length;
  }

  @Override
  public T classify(C ignored) {
    index++;
    if (index == rounds) {
      index = 0;
    }
    return targets.get(index);
  }
}
