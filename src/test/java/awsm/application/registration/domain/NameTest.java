package awsm.application.registration.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("a name")
class NameTest {

  @Test
  void can_be_turned_to_string() {
    var name = new FullName("Eduards", "Sizovs");
    assertThat(name + "").isEqualTo("Eduards Sizovs");

  }

}