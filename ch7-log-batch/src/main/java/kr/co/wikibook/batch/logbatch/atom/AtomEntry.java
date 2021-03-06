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

  public void setTitle(String title) {
    this.title = title;
  }

  public void setUpdated(Instant updated) {
    this.updated = updated;
  }

  public void setLink(Link link) {
    this.link = link;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public static class Link {

    private String href;

    public Link() {
    }

    public Link(String href) {
      this.href = href;
    }

    public String getHref() {
      return href;
    }

    @XmlAttribute
    public void setHref(String href) {
      this.href = href;
    }
  }

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

    public void setName(String name) {
      this.name = name;
    }
  }
}
