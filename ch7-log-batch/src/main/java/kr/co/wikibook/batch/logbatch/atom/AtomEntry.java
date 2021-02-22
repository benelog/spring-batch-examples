package kr.co.wikibook.batch.logbatch.atom;

import java.time.Instant;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import kr.co.wikibook.batch.logbatch.InstantAdapter;

@XmlRootElement(name = "entry")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomEntry {
  private String title;
  @XmlJavaTypeAdapter(InstantAdapter.class)
  private Instant updated;
  private Link link;
  private Author author;
  private String content;

  public AtomEntry(String title, Instant updated,
      Link link, Author author, String content) {
    this.title = title;
    this.updated = updated;
    this.link = link;
    this.author = author;
    this.content = content;
  }

  public AtomEntry() {
    // JAXB를 위해 디폴트 생성자가 필요함
  }

  public String getTitle() {
    return title;
  }

  public Instant getUpdated() {
    return updated;
  }

  public Link getLink() {
    return link;
  }

  public Author getAuthor() {
    return author;
  }

  public String getContent() {
    return content;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Link {

    @XmlAttribute
    private String href;

    public Link() {
    }

    public Link(String href) {
      this.href = href;
    }

    public String getHref() {
      return href;
    }

  }

  @XmlAccessorType(XmlAccessType.FIELD)

  public static class Author {

    private String name;

    public Author(String name) {
      this.name = name;
    }

    public Author() {
    }

    public String getName() {
      return name;
    }
  }
}
