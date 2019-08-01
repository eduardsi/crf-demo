package lightweight4j.features.registration.impl;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void rejectsNulls() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Email(null);
        });
    }

    @Test
    void rejectsBlankValues() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Email(" ");
        });
    }

    @Test
    void toStringReturnsAnEmail() {
        assertThat(new Email("whatever@email.com").toString())
                .isEqualTo("whatever@email.com");
    }

}