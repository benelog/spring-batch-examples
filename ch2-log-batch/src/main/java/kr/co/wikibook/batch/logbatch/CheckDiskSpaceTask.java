package kr.co.wikibook.batch.logbatch;

import java.io.File;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CheckDiskSpaceTask implements CommandLineRunner {
  private final Logger logger;

  public CheckDiskSpaceTask(ILoggerFactory loggerFactory) {
    this.logger = loggerFactory.getLogger(CheckDiskSpaceTask.class.getName());
  }

  @Override
  public void run(String... args) {
    if (args.length < 2) {
      return;
    }
    String directory = args[0];
    int minUsablePercentage = Integer.parseInt(args[1]);
    var file = new File(directory);
    int actualUsablePercentage = (int) (file.getUsableSpace() * 100 / file.getTotalSpace());
    logger.info("남은 용량 {}%", actualUsablePercentage);
    if (actualUsablePercentage < minUsablePercentage) {
      throw new IllegalStateException("디스크 용량이 기대치보다 작습니다 : " + actualUsablePercentage + "% 사용 가능");
    }
  }
}
