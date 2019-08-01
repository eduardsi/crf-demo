package lightweight4j.features.registration.impl;

import com.google.common.testing.NullPointerTester;
import org.junit.jupiter.api.Test;

class NameTest {

    @Test
    void rejectsNullInAConstructor() {
        new NullPointerTester()
                .testConstructors(Name.class, NullPointerTester.Visibility.PACKAGE);
    }

}