package kr.co.wikibook.batch.healthcheck;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource
public class HelloService {
  @ManagedOperation
  public void say() {
    System.out.println("hello!");
  }
}
