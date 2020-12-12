package kr.co.wikibook.batch.logbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class LogBatchApplication {
  public static void main(String[] args) {
    SpringApplication.run(LogBatchApplication.class, args);
  }
}
