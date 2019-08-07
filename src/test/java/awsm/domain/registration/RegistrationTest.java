package awsm.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import awsm.application.commands.Registration;
import awsm.infra.pipeline.tx.Tx;
import awsm.infra.pipeline.validation.ValidationException;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RegistrationTest {

  private final Faker fake = new Faker();

  @Test
  void registers_a_new_member_and_returns_its_id() {
    var id = new Tx<>(
            new Registration(fake.internet().emailAddress(), fake.name().firstName(), fake.name().lastName())).execute();
    assertThat(id).isInstanceOf(Long.class);
  }

  @Test
  void throws_if_email_is_missing() {
    assertThrows(ValidationException.class, () ->
            new Registration("", fake.name().firstName(), fake.name().lastName()).execute());
  }

  @Test
  void throws_if_first_name_is_missing() {
    assertThrows(ValidationException.class, () ->
            new Registration(fake.internet().emailAddress(), "", fake.name().lastName()).execute());
  }

  @Test
  void throws_if_last_name_is_missing() {
    assertThrows(ValidationException.class, () ->
            new Registration(fake.internet().emailAddress(), fake.name().firstName(), "").execute());
  }
}
