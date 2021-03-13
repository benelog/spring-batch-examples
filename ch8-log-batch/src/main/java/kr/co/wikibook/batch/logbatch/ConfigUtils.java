package kr.co.wikibook.batch.logbatch;

import org.springframework.beans.factory.InitializingBean;

public class ConfigUtils {

  public static <T extends InitializingBean> T afterPropertiesSet(T bean) {
    try {
      bean.afterPropertiesSet();
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
    return bean;
  }
}
