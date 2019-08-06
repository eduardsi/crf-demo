package lightweight4j.domain.registration;

import com.google.common.testing.NullPointerTester;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NameTest {

    @Test
    void can_be_turned_to_string() {
        var name = new Name("Eduards", "Sizovs");
        assertThat(name + "").isEqualTo("Eduards Sizovs");

    }

}