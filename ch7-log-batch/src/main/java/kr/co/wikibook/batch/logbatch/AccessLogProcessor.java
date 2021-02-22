package kr.co.wikibook.batch.logbatch;

import org.springframework.batch.item.ItemProcessor;

public class AccessLogProcessor implements ItemProcessor<AccessLog, AccessLog> {
  @Override
  public AccessLog process(AccessLog item) {
    if("127.0.0.1".equals(item.getIp())) {
      return null;
    }
    return item;
  }
}