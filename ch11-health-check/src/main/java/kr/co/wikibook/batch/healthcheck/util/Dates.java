package kr.co.wikibook.batch.healthcheck.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.util.Set;

public class Dates {
  private static final Set<MonthDay> LEGAL_HOLIDAYS = Set.of(
      MonthDay.of(Month.MAY, 5), // 어린이날
      MonthDay.of(Month.AUGUST, 15), // 광복절
      MonthDay.of(Month.DECEMBER, 25) // 크리스마스

  );

  public static boolean isHoliday(LocalDate day) {
    DayOfWeek dayOfWeek = day.getDayOfWeek();
    if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
      return true;
    }

    MonthDay monthDay = MonthDay.of(day.getMonth(), day.getDayOfMonth());
    if (LEGAL_HOLIDAYS.contains(monthDay)) {
      return true;
    }
    return false;
  }
}
