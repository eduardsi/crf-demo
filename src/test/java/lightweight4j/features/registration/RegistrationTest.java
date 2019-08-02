package lightweight4j.features.registration;

import com.github.javafaker.Faker;
import lightweight4j.lib.pipeline.Tx;
import lightweight4j.lib.pipeline.validation.CommandValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RegistrationTest {

    private Faker $ = new Faker();

    @Test
    void registersANewMemberAndReturnsItsId() {
        var id = new Tx<>(
                    new Registration($.internet().emailAddress(), $.name().firstName(), $.name().lastName())).execute();
        assertThat(id).isInstanceOf(Long.class);
    }

    @Test
    void throwsIfEmailIsMissing() {
        assertThrows(CommandValidationException.class, () -> {
            new Registration("", $.name().firstName(), $.name().lastName()).execute();
        });
    }

    @Test
    void throwsIfFirstNameIsMissing() {
        assertThrows(CommandValidationException.class, () -> {
            new Registration($.internet().emailAddress(), "", $.name().lastName()).execute();
        });
    }

    @Test
    void throwsIfLastNameIsMissing() {
        assertThrows(CommandValidationException.class, () -> {
            new Registration($.internet().emailAddress(), $.name().firstName(), "").execute();
        });
    }
}
