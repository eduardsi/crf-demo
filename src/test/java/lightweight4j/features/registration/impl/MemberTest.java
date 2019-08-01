package lightweight4j.features.registration.impl;

import com.google.common.testing.NullPointerTester;
import org.junit.jupiter.api.Test;

import static com.google.common.testing.NullPointerTester.*;

class MemberTest {

    @Test
    void rejectsNullInAConstructor() {
        new NullPointerTester()
                .setDefault(Email.class, new Email("x"))
                .setDefault(Name.class, new Name("Alan", "Walker"))
                .testConstructors(Member.class, Visibility.PACKAGE);
    }

}