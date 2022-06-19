package kr.co.wikibook.batch.healthcheck.support;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

public class Transactions {
  public static final TransactionAttribute TX_NOT_SUPPORTED = new DefaultTransactionAttribute(Propagation.NOT_SUPPORTED.value());

}
