package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonObjectMarshaller;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

class AccessLogJsonWriterTest {

  @Test
  void write(@TempDir Path tempPath) throws Exception {
    // given
    String outputPath = tempPath.toString() + "/access-log.json";
    var resource = new FileSystemResource(outputPath);
    JsonObjectMarshaller<AccessLog> jsonObjectMarshaller = this.buildJsonObjectMarshaller();
    var writer = new JsonFileItemWriter<>(resource, jsonObjectMarshaller);
    writer.afterPropertiesSet();
    var item = new AccessLog(Instant.parse("2019-10-10T11:14:16Z"), "127.0.0.1", "benelog");

    // when
    writer.open(new ExecutionContext());
    writer.write(List.of(item));
    writer.close();

    // then
    String jsonOutput = Files.readString(Path.of(outputPath));
    assertThat(jsonOutput).contains(
        "{\"accessDateTime\":\"2019-10-10T11:14:16Z\",\"ip\":\"127.0.0.1\",\"username\":\"benelog\"}"
    );
  }

  private JsonObjectMarshaller<AccessLog> buildJsonObjectMarshaller() {
    ObjectMapper objectMapper = new ObjectMapper()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .registerModule(new JavaTimeModule());
    return new JacksonJsonObjectMarshaller<>(objectMapper);
  }
}
