package awsm.application.registration.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("an email")
class EmailTest {

  @Test
  void rejects_blank_values() {
    assertThrows(IllegalArgumentException.class, () -> uniqueAndNotBlacklisted(" "));
  }

  @Test
  void can_be_turned_to_string() {
    var email = uniqueAndNotBlacklisted("whatever@email.com");
    assertThat(email + "").isEqualTo("whatever@email.com");
  }

  private Email uniqueAndNotBlacklisted(String email) {
    return new Email(email, EmailUniqueness.ALWAYS_GUARANTEED, EmailBlacklist.ALWAYS_ALLOWS);
  }

}