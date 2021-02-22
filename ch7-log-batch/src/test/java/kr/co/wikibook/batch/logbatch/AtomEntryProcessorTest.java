package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import kr.co.wikibook.batch.logbatch.atom.AtomEntry;
import kr.co.wikibook.batch.logbatch.atom.AtomEntry.Author;
import kr.co.wikibook.batch.logbatch.atom.AtomEntry.Link;
import org.junit.jupiter.api.Test;

class AtomEntryProcessorTest {

  AtomEntryProcessor processor = new AtomEntryProcessor();

  @Test
  void process() {
    var updatedAt = Instant.now();
    var url = "https://blog.benelog.net/installing-jdk.html";
    var title = "JDK 설치하기";
    var entry = new AtomEntry(
        title,
        updatedAt,
        new Link(url),
        new Author("정상혁"),
        "JDK를 이렇게 설치해야 합니다."
    );

    BlogPost post = processor.process(entry);

    assertThat(post.getTitle()).isEqualTo(title);
    assertThat(post.getUrl()).isEqualTo(url);
    assertThat(post.getUpdatedAt()).isEqualTo(updatedAt);
  }
}