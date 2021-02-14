package kr.co.wikibook.batch.logbatch;

import org.springframework.classify.Classifier;

public class OddEvenClassifier<T> implements Classifier<Integer, T> {

  private final T oddTarget;
  private final T evenTarget;

  public OddEvenClassifier(T oddTarget, T evenTarget) {
    this.oddTarget = oddTarget;
    this.evenTarget = evenTarget;
  }

  @Override
  public T classify(Integer classifiable) {
      if( classifiable % 2 == 0) {
        return evenTarget;
      }
      return oddTarget;
  }
}
