package kr.co.wikibook.batch.healthcheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.net.http.HttpConnectTimeoutException;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CallUriProcessorTest {

  @Test
  @DisplayName("200 OK 응답을 받는다")
  void processOk() throws Exception {
    CallUriProcessor processor = new CallUriProcessor(Duration.ofSeconds(3));
    ResponseStatus responseStatus = processor.process("https://benelog.net");
    assertThat(responseStatus.getStatusCode()).isEqualTo(200);
  }

  @Test
  @DisplayName("타입아웃 예외가 발생한다")
  void processWhenTimeout() {
    CallUriProcessor processor = new CallUriProcessor(Duration.ofMillis(1));
    assertThatExceptionOfType(HttpConnectTimeoutException.class)
        .isThrownBy(() ->
            processor.process("https://benelog.net")
        );
  }

  @Test
  @DisplayName("부적절한 URI 형식이라 예외가 발생한다")
  void processWhenInValidUriFormat() {
    CallUriProcessor processor = new CallUriProcessor(Duration.ofMillis(2000));
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() ->
            processor.process("benelog.net")
        );
  }
}
