package awsm.application.registration.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("an email")
class EmailTest {

  @Test
  void rejects_blank_values() {
    assertThrows(IllegalArgumentException.class, () -> new Email(" "));
  }

  @Test
  void can_be_turned_to_string() {
    var email = new Email("whatever@email.com");
    assertThat(email + "").isEqualTo("whatever@email.com");
  }

}