package kr.co.wikibook.batch.logbatch;

import kr.co.wikibook.batch.logbatch.atom.AtomEntry;
import org.springframework.batch.item.ItemProcessor;

public class AtomEntryProcessor implements ItemProcessor<AtomEntry, BlogPost> {

  @Override
  public BlogPost process(AtomEntry entry) {
    return new BlogPost(
        entry.getTitle(),
        entry.getLink().getHref(),
        entry.getUpdated()
    );
  }
}
