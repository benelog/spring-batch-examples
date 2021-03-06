package kr.co.wikibook.batch.logbatch;

import java.time.Instant;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "post")
public class BlogPost {
  private String title;
  private String url;
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

  @XmlJavaTypeAdapter(InstantAdapter.class)
  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
