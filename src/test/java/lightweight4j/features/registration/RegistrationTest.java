package lightweight4j.features.registration;

import lightweight4j.lib.pipeline.validation.CommandValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RegistrationTest {

    @Test
    void registersANewMemberAndReturnsItsId() {
        var id = new Registration("hello@devternity.com", "Uncle", "Bob").execute();
        assertThat(id).isInstanceOf(Long.class);
    }

    @Test
    void throwsIfEmailIsMissing() {
        assertThrows(CommandValidationException.class, () -> {
            new Registration("", "Uncle", "Bob").execute();
        });
    }

    @Test
    void throwsIfFirstNameIsMissing() {
        assertThrows(CommandValidationException.class, () -> {
            new Registration("hello@devternity.com", "", "Bob").execute();
        });
    }

    @Test
    void throwsIfLastNameIsMissing() {
        assertThrows(CommandValidationException.class, () -> {
            new Registration("hello@devternity.com", "Uncle", "").execute();
        });
    }
}
