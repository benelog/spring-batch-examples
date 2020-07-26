package kr.co.wikibook.batch.logbatch;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

public class CheckDiskSpaceTask implements CommandLineRunner {

  private final Logger logger = LoggerFactory.getLogger(CountAccessLogTask.class);

  @Override
  public void run(String... args) {
    if (args.length == 0) {
      return;
    }
    String directory = args[0];
    int expectedUsablePercentage = Integer.parseInt(args[1]);
    File file = new File(directory);
    int actualUsablePercentage = (int) (file.getUsableSpace() * 100 / file.getTotalSpace());
    logger.info("남은 용량 {}%", actualUsablePercentage);
    if (actualUsablePercentage < expectedUsablePercentage) {
      throw new IllegalStateException("디스크 용량이 기대치보다 작습니다 : " + actualUsablePercentage + "% 사용 가능");
    }
  }
}
