package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonObjectMarshaller;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

class AccessLogJsonMultiResourceWriterTest {

  @Test
  void writeMulti(@TempDir Path tempPath) throws Exception {
    // given
    String outputPath = tempPath.toString() + "/access-log.json";
    Resource resource = new FileSystemResource(outputPath);

    MultiResourceItemWriter<AccessLog> writer = constructMultiResourceItemWriter(resource);

    var item1 = new AccessLog(Instant.parse("2019-10-10T11:14:16Z"), "127.0.0.1", "benelog");
    var item2 = new AccessLog(Instant.parse("2019-10-10T11:15:23Z"), "127.0.0.1", "benelog");

    // when
    writer.open(new ExecutionContext());
    writer.write(List.of(item1));
    writer.write(List.of(item2));
    writer.close();

    // then
    assertThat(tempPath.resolve("access-log.json.1")).exists();
    assertThat(tempPath.resolve("access-log.json.2")).exists();
  }

  private MultiResourceItemWriter<AccessLog> constructMultiResourceItemWriter(Resource resource)
      throws Exception {

    var jsonWriter = new JsonFileItemWriter<>(resource, constructJsonObjectMarshaller());
    jsonWriter.afterPropertiesSet();

    return new MultiResourceItemWriterBuilder<AccessLog>()
        .name("accessLogJsonMultiWriter")
        .resource(resource)
        .delegate(jsonWriter)
        .itemCountLimitPerResource(1)
        .build();
  }

  private JsonObjectMarshaller<AccessLog> constructJsonObjectMarshaller() {
    ObjectMapper objectMapper = new ObjectMapper()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .registerModule(new JavaTimeModule());

    JacksonJsonObjectMarshaller<AccessLog> marshaller = new JacksonJsonObjectMarshaller<>();
    marshaller.setObjectMapper(objectMapper);
    return marshaller;
  }
}
