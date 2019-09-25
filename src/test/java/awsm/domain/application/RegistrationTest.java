package awsm.domain.application;

import static com.machinezoo.noexception.Exceptions.sneak;
import static com.pivovarit.collectors.ParallelCollectors.parallelToList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import awsm.infra.jackson.MsgPack;
import com.github.javafaker.Faker;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.msgpack.core.MessagePack;
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
@DisplayName("registration")
class RegistrationTest {

  @Autowired
  private MockMvc mvc;

  @Nested
  @DisplayName("with message pack")
  class MsgPackHttp {

    @Test
    void returns_a_member_id_upon_completion() throws Exception {
      var packer = MessagePack.newDefaultBufferPacker();
      packer
          .packMapHeader(3)
          .packString("email")
          .packString("eduards@sizovs.net")
          .packString("firstName")
          .packString("Eduards")
          .packString("lastName")
          .packString("Sizovs")
          .close();

      var response = mvc.perform(post("/members")
          .content(packer.toByteArray())
          .accept(MsgPack.MIME)
          .contentType(MsgPack.MIME))
          .andExpect(status().isOk())
          .andReturn().getResponse().getContentAsByteArray();

      var unpacker = MessagePack.newDefaultUnpacker(response);
      var id = unpacker.unpackString();

      assertThat(id).matches("[a-zA-Z0-9]{10}");
    }

  }

  @Nested
  @DisplayName("with json")
  class JsonHttp {

    @Test
    void rate_limits_to_one_registration_at_a_time() {
      var executor = Executors.newFixedThreadPool(2);
      var responses = Stream.of(email(), email())
          .collect(parallelToList(this::register, executor))
          .join()
          .stream()
          .map(ResultActions::andReturn)
          .map(MvcResult::getResponse)
          .collect(toList());

      assertThat(responses).haveAtLeastOne(new Condition<>(ex -> ex.getStatus() == 200, "status OK"));
      assertThat(responses).haveAtLeastOne(new Condition<>(ex -> ex.getStatus() == 429, "status too many requests"));
    }

    @Test
    void returns_a_hashed_member_id_upon_completion() throws Exception {
      register()
          .andExpect(status().isOk())
          .andExpect(content().string(hasLength(10)));
    }

    @Test
    void throws_if_email_is_not_unique() throws Exception {
      var email = email();
      register(email)
          .andExpect(status().isOk());

      register(email)
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0]", is("email is taken")));
    }

    @Test
    void throws_if_email_or_first_name_or_last_name_are_missing() throws Exception {
      register("", "", "")
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0]", is("firstName is missing")))
          .andExpect(jsonPath("$.[1]", is("lastName is missing")))
          .andExpect(jsonPath("$.[2]", is("email is missing")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pornhub.com", "rotten.com"})
    void throws_if_email_is_blacklisted(String domain) throws Exception {
      var email = "eduards@" + domain;
      register(email)
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0]", is("email " + email + " is blacklisted")));
    }

    private ResultActions register() {
      return this.register(email());
    }

    private ResultActions register(String email) {
      return register(email, "Eduards", "Sizovs");
    }

    private ResultActions register(String email, String firstName, String lastName) {
      var json = "{" + "\"email\": \"" + email + "\"," + "\"firstName\": \""
          + firstName + "\"," + "\"lastName\": \"" + lastName + "\"" + "}";
      return sneak().get(() -> mvc.perform(post("/members")
          .content(json)
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)));
    }

  }

  private static String email() {
    return new Faker().internet().emailAddress();
  }
}
