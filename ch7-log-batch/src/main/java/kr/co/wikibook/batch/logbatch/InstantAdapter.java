package kr.co.wikibook.batch.logbatch;

import java.time.Instant;
import javax.xml.bind.annotation.adapters.XmlAdapter;

// ISO 8601 형식 (yyyy-MM-dd'T'HH:mm:ss'Z')의 문자열과 Instant를 상호 변환
public class InstantAdapter extends XmlAdapter<String, Instant> {
  @Override
  public Instant unmarshal(String text) {
    return Instant.parse(text);
  }

  @Override
  public String marshal(Instant bounded) {
    return bounded.toString();
  }
}
