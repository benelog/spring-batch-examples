package kr.co.wikibook.batch.healthcheck.util;

import java.time.Instant;

public class Times {
  // 2개의 Instant의 차이를 '시:분:초' 형식으로 반환
  public static String getReadableDuration(Instant from, Instant to) {
    long durationSeconds = to.getEpochSecond() - from.getEpochSecond();
    long hours = durationSeconds / 60 / 60;
    long leftSeconds = durationSeconds - hours * 60 * 60;
    long minuets = leftSeconds / 60;
    long seconds = leftSeconds - minuets * 60;
    return String.format("%d:%02d:%02d", hours, minuets, seconds);
  }
}
