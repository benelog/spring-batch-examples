package kr.co.wikibook.batch.logbatch;

import java.time.Instant;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "post")
@XmlAccessorType(XmlAccessType.FIELD)
public class BlogPost {
  private String title;
  private String url;

  @XmlJavaTypeAdapter(InstantAdapter.class)
  private Instant updatedAt;

  public BlogPost() {
    // Jaxb를 위한 생성자
  }

  public BlogPost(String title, String url, Instant updatedAt) {
    this.title = title;
    this.url = url;
    this.updatedAt = updatedAt;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
