package awsm.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.testing.EqualsTester;
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

  @Test
  void has_proper_equals_and_hash_code() {
    new EqualsTester()
        .addEqualityGroup(new Email("one@domain.com"), new Email("one@domain.com"))
        .addEqualityGroup(new Email("another@domain.com"))
        .testEquals();
  }

}