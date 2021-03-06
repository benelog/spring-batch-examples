package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.JsonObjectReader;
import org.springframework.core.io.ClassPathResource;

class AccessLogJsonReaderTest {

  @Test
  void read() throws Exception {
    var resource = new ClassPathResource("sample-access-log.json");
    JsonObjectReader<AccessLog> jsonObjectReader = this.buildJsonObjectReader();
    var reader = new JsonItemReader<>(resource, jsonObjectReader);
    reader.open(new ExecutionContext());
    AccessLog item = reader.read();
    reader.close();

    assertThat(item.getAccessDateTime()).isEqualTo("2019-10-10T11:14:16Z");
    assertThat(item.getIp()).isEqualTo("175.242.91.54");
    assertThat(item.getUsername()).isEqualTo("benelog");
  }

  JsonObjectReader<AccessLog> buildJsonObjectReader() {
    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    var jsonObjectReader = new JacksonJsonObjectReader<>(AccessLog.class);
    jsonObjectReader.setMapper(objectMapper);
    return jsonObjectReader;
  }
}
