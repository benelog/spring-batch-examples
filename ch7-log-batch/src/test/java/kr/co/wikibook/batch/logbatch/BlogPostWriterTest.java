package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

class BlogPostWriterTest {

  @Test
  void write(@TempDir Path tempPath) throws Exception {
    // given
    String outputPath = tempPath.toString() + "/blogPosts.xml";
    Instant updatedAt = ZonedDateTime.of(
        LocalDateTime.of(2020, 2, 2, 16, 2),
        ZoneOffset.UTC
    ).toInstant();
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
    assertThat(output).contains("<updatedAt>2020-02-02T16:02:00Z</updatedAt>");
  }
}
