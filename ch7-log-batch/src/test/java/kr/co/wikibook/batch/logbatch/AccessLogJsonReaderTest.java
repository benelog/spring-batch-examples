package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.JsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class AccessLogJsonReaderTest {

  @Test
  void read() throws Exception {
    Resource resource = new ClassPathResource("sample-access-log.json");
    JsonObjectReader<AccessLog> jsonObjectReader = constructJsonObjectReader();

    JsonItemReader<AccessLog> reader = new JsonItemReaderBuilder<AccessLog>()
        .name("accessLogJsonReader")
        .resource(resource)
        .jsonObjectReader(jsonObjectReader)
        .build();

    reader.open(new ExecutionContext());
    AccessLog item = reader.read();
    reader.close();

    assertThat(item.getAccessDateTime()).isEqualTo("2019-10-10T11:14:16Z");
    assertThat(item.getIp()).isEqualTo("175.242.91.54");
    assertThat(item.getUsername()).isEqualTo("benelog");
  }

  private JsonObjectReader<AccessLog> constructJsonObjectReader() {
    ObjectMapper objectMapper = new ObjectMapper()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .registerModule(new ParameterNamesModule())
        .registerModule(new JavaTimeModule());

    // ParameterNamesModule 이 없으면 아래 에러 발생
    //    Caused by: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `kr.co.wikibook.batch.loganalysis.AccessLog` (no Creators, like default construct, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
    //    at [Source: (BufferedInputStream); line: 2, column: 4]
    // IntelliJ compiler에서는 constructor의 파라미터 이름이 인지가 안 되는 문제 확인 필요

    var jsonObjectReader = new JacksonJsonObjectReader<>(AccessLog.class);
    jsonObjectReader.setMapper(objectMapper);
    return jsonObjectReader;
  }
}
