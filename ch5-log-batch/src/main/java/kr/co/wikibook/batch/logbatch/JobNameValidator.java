package kr.co.wikibook.batch.logbatch;

import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class JobNameValidator {

  public JobNameValidator(BatchProperties properties) {
    String jobNames = properties.getJob().getNames();
    Assert.hasText(jobNames, "spring.batch.job.names 속성이 지정되어 있어야한다.");
  }
}
