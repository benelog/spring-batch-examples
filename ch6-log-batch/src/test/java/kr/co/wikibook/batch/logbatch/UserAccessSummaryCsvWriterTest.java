package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

class UserAccessSummaryCsvWriterTest {

  @Test
  void write(@TempDir Path tempPath) throws Exception {
    // given
    String outputPath = tempPath.toString() + "/user-access-summary.csv";
    var resource = new FileSystemResource(outputPath);
    var items = List.of(
        new UserAccessSummary("benelog", 32),
        new UserAccessSummary("jojoldu", 42)
    );

    // when
    var writer = new UserAccessSummaryCsvWriter(resource);
    writer.open(new ExecutionContext());
    writer.write(items);
    writer.close();

    // then
    List<String> written = Files.readAllLines(Path.of(outputPath));
    assertThat(written).isEqualTo(List.of("benelog,32", "jojoldu,42"));
  }
}
