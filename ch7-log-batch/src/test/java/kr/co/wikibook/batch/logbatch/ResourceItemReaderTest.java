package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

class ResourceItemReaderTest {

  @Test
  void readResources(@TempDir Path tempPath) throws Exception {
    // given
    createFile(tempPath, "1.txt", "2.txt", "3.txt");
    String txtFilePattern = "file:" + tempPath + "/*.txt";
    ResourcesItemReader reader = buildResourceItemReader(txtFilePattern);

    // when, then
    reader.open(new ExecutionContext());
    assertThat(reader.read().getFilename()).isEqualTo("1.txt");
    assertThat(reader.read().getFilename()).isEqualTo("2.txt");
    assertThat(reader.read().getFilename()).isEqualTo("3.txt");
    assertThat(reader.read()).isNull();
    reader.close();
  }

  private void createFile(Path directory, String... fileNames) throws IOException {
    for (String each : fileNames) {
      Path file = directory.resolve(Path.of(each));
      Files.write(file, List.of("test content"));
    }
  }

  private ResourcesItemReader buildResourceItemReader(String locationPattern) throws IOException {
    var resourcePatternResolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = resourcePatternResolver.getResources(locationPattern);
    var reader = new ResourcesItemReader();
    reader.setResources(resources);
    return reader;
  }
}
