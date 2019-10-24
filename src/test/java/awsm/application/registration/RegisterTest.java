package awsm.application.registration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.machinezoo.noexception.Exceptions.sneak;
import static com.pivovarit.collectors.ParallelCollectors.parallelToList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import awsm.AwesomeBank;
import com.github.javafaker.Faker;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = { AwesomeBank.class })
@Rollback
@DisplayName("registration")
class RegisterTest {

  private MockMvc mvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Value("${blacklist.port}")
  private int blacklistPort;

  @Value("${registration.rateLimit}")
  private int registrationRateLimit;

  private WireMockServer blacklist;

  @BeforeEach
  void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    blacklist = new WireMockServer(blacklistPort);
    blacklist.start();

    blacklist.stubFor(get(anyUrl())
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("ALLOW")));

    blacklist.stubFor(get(urlPathMatching(".*(rotten.com|pornhub.com)"))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("BLOCK")));
  }

  @AfterEach
  void cleaup() {
    blacklist.stop();
  }

  @Test
  void rate_limits_to_N_registration_at_a_time() {
    var overflow = registrationRateLimit + 1;
    var executor = Executors.newFixedThreadPool(overflow);
    var responses = Stream.generate(() -> email()).limit(overflow)
        .collect(parallelToList(this::register, executor))
        .join()
        .stream()
        .map(ResultActions::andReturn)
        .map(MvcResult::getResponse)
        .collect(toList());

    assertThat(responses).haveExactly(registrationRateLimit, new Condition<>(ex -> ex.getStatus() == 200, "status OK"));
    assertThat(responses).haveExactly(1, new Condition<>(ex -> ex.getStatus() == 429, "status too many requests"));
  }

  @Test
  void returns_a_hashed_customer_id_upon_completion() throws Exception {
    register()
        .andExpect(status().isOk())
        .andExpect(content().string(hasLength(10)));

    Thread.sleep(9999999);
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
    return sneak().get(() -> mvc.perform(post("/customers")
        .content(json)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)));
  }

  private static String email() {
    return new Faker().internet().emailAddress();
  }
}
