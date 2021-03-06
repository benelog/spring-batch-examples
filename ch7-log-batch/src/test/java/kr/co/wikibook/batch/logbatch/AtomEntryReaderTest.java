package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import kr.co.wikibook.batch.logbatch.atom.AtomEntry;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.core.io.ClassPathResource;

class AtomEntryReaderTest {

  @Test
  void readItems() throws Exception {
    // given
    var resource = new ClassPathResource("blog.atom");
    var jobConfig = new CollectBlogPostJobConfig();
    StaxEventItemReader<AtomEntry> reader = jobConfig.atomEntryReader(resource);
    reader.afterPropertiesSet();

    // when
    reader.open(new ExecutionContext());
    AtomEntry item = reader.read();
    reader.close();

    // then
    assertThat(item.getTitle()).isEqualTo("This Week in Spring - February 23rd, 2021");
    assertThat(item.getAuthor().getName()).isEqualTo("Josh Long");
    assertThat(item.getLink().getHref()).startsWith("https://spring.io/blog/");
    assertThat(item.getUpdated()).isEqualTo(Instant.parse("2021-02-24T03:18:00Z"));
    assertThat(item.getContent()).isNotBlank();
 }
}