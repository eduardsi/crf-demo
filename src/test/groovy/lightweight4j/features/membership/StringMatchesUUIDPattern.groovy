package lightweight4j.features.membership

import org.hamcrest.Description
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

class StringMatchesUUIDPattern extends TypeSafeMatcher<String> {

    private static final String UUID_REGEX = "[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}"

    @Override
    protected boolean matchesSafely(String s) {
        s.matches(UUID_REGEX)
    }

    @Override
    void describeTo(Description description) {
        description.appendText("a string matching the pattern of a UUID")
    }

    static Matcher<String> matchesUuid() {
        new StringMatchesUUIDPattern()
    }

}