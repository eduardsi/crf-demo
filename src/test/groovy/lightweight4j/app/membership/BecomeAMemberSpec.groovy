package lightweight4j.app.membership

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest

import static StringMatchesUUIDPattern.matchesUuid
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static groovy.json.JsonOutput.toJson
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class BecomeAMemberSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    def "creates a new member via http and returns its id"() {
        given:
            def request = [
                    email: 'eduards@sizovs.net'
            ]
        when:
            def _ = mockMvc.perform(post("/members").contentType(APPLICATION_JSON).content(toJson(request))).andDo(print())
        then:
            _.andExpect(status().isOk())

        and:
            _.andExpect(content().string(matchesUuid()))
    }

}

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