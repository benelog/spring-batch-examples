package kr.co.wikibook.batch.logbatch;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class InstantAdapterTest {

  InstantAdapter adapter = new InstantAdapter();
  String iso8601Text = "2020-02-02T16:02:00Z";
  Instant instant = Instant.parse(iso8601Text);

  @Test
  void marshal() {
    String marshalled = adapter.marshal(instant);
    assertThat(marshalled).isEqualTo(iso8601Text);
  }

  @Test
  void unmarshal() {
    Instant unmarshalled = adapter.unmarshal(iso8601Text);
    assertThat(unmarshalled).isEqualTo(instant);
  }
}