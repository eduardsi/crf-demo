package awsm.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NameTest {

  @Test
  void can_be_turned_to_string() {
    var name = new Name("Eduards", "Sizovs");
    assertThat(name + "").isEqualTo("Eduards Sizovs");

  }

}