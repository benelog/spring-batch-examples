package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.core.io.FileSystemResource;

class BlogPostWriterTest {

  @Test
  void write(@TempDir Path tempPath) throws Exception {
    // given
    String outputPath = tempPath.toString() + "/blogPosts.xml";
    Instant updatedAt = Instant.parse("2021-03-10T11:14:16Z");
    var resource = new FileSystemResource(outputPath);
    var post = new BlogPost(
        "JDK 설치하기",
        "https://blog.benelog.net/installing-jdk.html",
        updatedAt
    );

    var jobConfig = new CollectBlogPostJobConfig();
    StaxEventItemWriter<BlogPost> writer = jobConfig.blogPostWriter(resource);
    writer.afterPropertiesSet();

    // when
    writer.open(new ExecutionContext());
    writer.write(List.of(post));
    writer.close();

    // then
    String output = Files.readString(Path.of(outputPath));
    assertThat(output).contains("<title>JDK 설치하기</title>");
    assertThat(output).contains("<url>https://blog.benelog.net/installing-jdk.html</url>");
    assertThat(output).contains("<updatedAt>2021-03-10T11:14:16Z</updatedAt>");
  }
}
