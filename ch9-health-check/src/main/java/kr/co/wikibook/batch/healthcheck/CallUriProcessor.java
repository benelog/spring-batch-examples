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
import org.springframework.batch.item.ItemProcessor;

public class CallUriProcessor implements ItemProcessor<String, ResponseStatus> {
  private final Logger logger = LoggerFactory.getLogger(CallUriProcessor.class);

  private final HttpClient client = HttpClient.newBuilder().build();
  private final Duration requestTimeout;

  public CallUriProcessor(Duration requestTimeout) {
    this.requestTimeout = requestTimeout;
  }

  @Override
  public ResponseStatus process(String rawUri)
      throws IOException, InterruptedException, URISyntaxException {

    logger.info("try to call {}", rawUri);
    URI uri = new URI(rawUri);
    HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        .timeout(this.requestTimeout)
        .build();
    long startTime = System.currentTimeMillis();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    long responseTime = System.currentTimeMillis() - startTime;
    logger.info("{}ms", responseTime);
    return new ResponseStatus(uri, response.statusCode(), responseTime);
  }
}
