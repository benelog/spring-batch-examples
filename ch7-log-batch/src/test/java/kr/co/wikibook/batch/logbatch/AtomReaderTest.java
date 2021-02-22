package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;

import kr.co.wikibook.batch.logbatch.atom.AtomEntry;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.core.io.ClassPathResource;

class AtomReaderTest {

  @Test
  void read() throws Exception {
    // given
    var resource = new ClassPathResource("blog.atom");
    var jobConfig = new CollectBlogPostJobConfig();
    StaxEventItemReader<AtomEntry> reader = jobConfig.atomEntryReader(resource);
    reader.afterPropertiesSet();

    // when
    reader.open(new ExecutionContext());
    AtomEntry entry = reader.read();
    reader.close();

    // then
    assertThat(entry.getTitle()).isNotBlank();
    assertThat(entry.getContent()).isNotBlank();
    assertThat(entry.getAuthor().getName()).isNotBlank();
    assertThat(entry.getLink().getHref()).startsWith("https://spring.io/blog/");
  }
}