package lightweight4j.domain.registration;

import com.github.javafaker.Faker;
import lightweight4j.application.commands.Registration;
import lightweight4j.infra.pipeline.tx.Tx;
import lightweight4j.infra.pipeline.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RegistrationTest {

    private Faker $ = new Faker();

    @Test
    void registers_a_new_member_and_returns_its_id() {
        var id = new Tx<>(
                    new Registration($.internet().emailAddress(), $.name().firstName(), $.name().lastName())).execute();
        assertThat(id).isInstanceOf(Long.class);
    }

    @Test
    void throws_if_email_is_missing() {
        assertThrows(ValidationException.class, () -> {
            new Registration("", $.name().firstName(), $.name().lastName()).execute();
        });
    }

    @Test
    void throws_if_first_name_is_missing() {
        assertThrows(ValidationException.class, () -> {
            new Registration($.internet().emailAddress(), "", $.name().lastName()).execute();
        });
    }

    @Test
    void throws_if_last_name_is_missing() {
        assertThrows(ValidationException.class, () -> {
            new Registration($.internet().emailAddress(), $.name().firstName(), "").execute();
        });
    }
}
