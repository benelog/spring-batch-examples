package kr.co.wikibook.batch.healthcheck;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

public class CallUriProcessor  implements
    ItemProcessor<String, ResponseStatus>,
    ItemProcessListener<String, ResponseStatus> {
  private final Logger logger = LoggerFactory.getLogger(CallUriProcessor.class);

  private final HttpClient client = HttpClient.newBuilder().build();
  private final Duration requestTimeout;

  public CallUriProcessor(Duration requestTimeout) {
    this.requestTimeout = requestTimeout;
  }

  @BeforeStep
  public void logRequestTimeout() {
    logger.info("requestTimeout : {} seconds", requestTimeout.getSeconds());
  }

  @Override
  public void beforeProcess(String rawUri) {
    logger.info("호출 시도 : {}", rawUri);
  }

  @Override
  public ResponseStatus process(String rawUri)
      throws IOException, InterruptedException, URISyntaxException {
    URI uri = new URI(rawUri);
    HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        .timeout(this.requestTimeout)
        .build();
    long startTime = System.currentTimeMillis();
    HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
    long responseTime = System.currentTimeMillis() - startTime;
    return new ResponseStatus(uri, response.statusCode(), responseTime);
  }

  @Override
  public void afterProcess(String rawUri, ResponseStatus result) {
    logger.info("응답 시간 : {}ms", result.getResponseTime());
    if(result.getStatusCode() == 404) {
      logger.warn("404 응답 : {}", rawUri);
    }
  }

  @Override
  public void onProcessError(String rawUri, Exception ex) {
    logger.warn("호출 실패 {}", rawUri, ex);
  }
}
