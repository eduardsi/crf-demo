package awsm.domain.registration;

import static com.machinezoo.noexception.Exceptions.sneak;
import static com.pivovarit.collectors.ParallelCollectors.parallelToList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.javafaker.Faker;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest
@AutoConfigureMockMvc
class RegisterTest {

  @Autowired
  private MockMvc mvc;

  private Faker faker = new Faker();

  @Test
  void rate_limits_to_one_registration_at_a_time()  {

    var executor = Executors.newFixedThreadPool(2);

    var responses = Stream.of(fakeEmail(), fakeEmail())
        .collect(parallelToList(this::registerWithEmail, executor))
        .join()
        .stream()
        .map(ResultActions::andReturn)
        .map(MvcResult::getResponse)
        .collect(toList());

    assertThat(responses).haveAtLeastOne(new Condition<>(ex -> ex.getStatus() == 200, "status OK"));
    assertThat(responses).haveAtLeastOne(new Condition<>(ex -> ex.getStatus() == 429, "status too many requests"));

  }

  @Test
  void registers_a_new_member_and_returns_its_id() throws Exception {
    register()
        .andExpect(status().isOk())
        .andExpect(content().string(anything()));
  }

  @Test
  void throws_if_email_is_missing() throws Exception {
    var json = "{" + "\"firstName\": \"Eduards\"," + "\"lastName\": \"Sizovs\"" + "}";
    mvc.perform(post("/members")
            .content(json)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.[0].property", is("email")))
            .andExpect(jsonPath("$.[0].message", is("must not be empty")));
  }

  @Test
  void throws_if_first_name_is_missing() throws Exception {
    var json = "{" + "\"email\": \"eduards@sizovs.net\"," + "\"lastName\": \"Sizovs\"" + "}";
    mvc.perform(post("/members")
            .content(json)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.[0].property", is("firstName")))
            .andExpect(jsonPath("$.[0].message", is("must not be empty")));
  }

  @Test
  void throws_if_last_name_is_missing() throws Exception {
    var json = "{" + "\"email\": \"eduards@sizovs.net\"," + "\"firstName\": \"Eduards\"" + "}";
    mvc.perform(post("/members")
            .content(json)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.[0].property", is("lastName")))
            .andExpect(jsonPath("$.[0].message", is("must not be empty")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"pornhub.com", "rotten.com"})
  void throws_if_email_is_blacklisted(String domain) throws Exception {
    var email = "eduards@" + domain;
    var json = "{" + "\"email\": \"" + email + "\"" + "," + "\"firstName\": \"Eduards\"," + "\"lastName\": \"Sizovs\"" + "}";
    mvc.perform(post("/members")
            .content(json)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string("Email " + email + " is blacklisted"))
            .andExpect(status().is(400));
  }

  private ResultActions register() {
    String email = fakeEmail();
    return registerWithEmail(email);
  }

  private String fakeEmail() {
    return faker.internet().emailAddress();
  }

  private ResultActions registerWithEmail(String email) {
    var json = "{" + "\"email\": \"" + email + "\"," + "\"firstName\": \"Eduards\"," + "\"lastName\": \"Sizovs\"" + "}";
    return sneak().get(() -> mvc.perform(post("/members")
        .content(json)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)));
  }
}
