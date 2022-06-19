package kr.co.wikibook.batch.healthcheck;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
@EnableBatchProcessing
public class HealthCheckApplication {
  public static void main(String[] args) throws IOException {
    SpringApplication.run(HealthCheckApplication.class, args);
  }
}
