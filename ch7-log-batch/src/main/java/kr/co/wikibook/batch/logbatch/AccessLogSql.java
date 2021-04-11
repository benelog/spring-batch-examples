package kr.co.wikibook.batch.logbatch;

public class AccessLogSql {

  public static final String INSERT =
      "INSERT INTO access_log(access_date_time, ip, username)"
          + "VALUES (:accessDateTime, :ip, :username)";

  public static final String COUNT_GROUP_BY_USERNAME =
      "SELECT username, COUNT(1) AS access_count\n"
          + "FROM access_log\n"
          + "GROUP BY username";
}
