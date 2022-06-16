package kr.co.wikibook.batch.healthcheck;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource
public class JmxTest  {
  @ManagedOperation
  public void test() {
    System.out.println("hello!");
  }

}
