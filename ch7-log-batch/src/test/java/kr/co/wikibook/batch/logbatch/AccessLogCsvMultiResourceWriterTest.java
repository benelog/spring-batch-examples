package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

class AccessLogCsvMultiResourceWriterTest {

  @Test
  void writeMulti(@TempDir Path tempPath) throws Exception {
    // given
    String outputPath = tempPath.toString() + "/access-log.txt";
    Resource resource = new FileSystemResource(outputPath);
    MultiResourceItemWriter<AccessLog> writer = this.buildMultiResourceItemWriter(resource, 1);
    var item1 = new AccessLog(Instant.parse("2019-10-10T11:14:16Z"), "127.0.0.1", "benelog");
    var item2 = new AccessLog(Instant.parse("2019-10-10T11:15:23Z"), "127.0.0.1", "benelog");

    // when
    writer.open(new ExecutionContext());
    writer.write(List.of(item1));
    writer.write(List.of(item2));
    writer.close();

    // then
    assertThat(tempPath.resolve("access-log.txt.1")).exists();
    assertThat(tempPath.resolve("access-log.txt.2")).exists();
  }

  MultiResourceItemWriter<AccessLog> buildMultiResourceItemWriter(
      Resource resource,
      int itemsPerResource) throws Exception {

    FlatFileItemWriter<AccessLog> delegate = this.buildDelegate(resource);
    delegate.afterPropertiesSet();
    return new MultiResourceItemWriterBuilder<AccessLog>()
        .name("accessLogJsonMultiWriter")
        .resource(resource)
        .delegate(delegate)
        .itemCountLimitPerResource(itemsPerResource)
        .build();
  }

  FlatFileItemWriter<AccessLog> buildDelegate(Resource resource) {
    return new FlatFileItemWriterBuilder<AccessLog>()
        .name("accessLogCsvWriter")
        .resource(resource)
        .delimited()
        .fieldExtractor((AccessLog item) -> new Object[]{
            item.getAccessDateTime(),
            item.getIp(),
            item.getUsername()
        })
        .build();
  }
}
