package awsm.domain.application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class TradingTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void accepting_a_pending_offer() throws Exception {
    var offerId = place("100.00")
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    accept(offerId, "100.00")
        .andExpect(status().isOk());
  }

  @Test
  void increasing_the_price() throws Exception {
    var offerId = place("100.00")
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    raise(offerId, "0.50")
        .andExpect(status().isOk())
        .andExpect(content().string("150.00"));
  }

  @Test
  void throwing_on_accepting_offer_that_is_already_accepted() throws Exception {
    var offerId = place("100.00")
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    accept(offerId, "100.00")
        .andExpect(status().isOk());

    accept(offerId, "100.00")
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Cannot accept ACCEPTED offer. Must be PENDING."));
  }

  @Test
  void throwing_on_accepting_offer_that_exceeds_the_limit() throws Exception {
    var offerId = place("100.00")
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    accept(offerId, "90.00")
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Offer price is not within a limit (100.00/90.00)"));
  }

  private ResultActions accept(String offerId, String limit) throws Exception {
    return mvc.perform(post("/offers/" + offerId + "/accept/limit/" + limit)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON));
  }

  private ResultActions raise(String offerId, String ratio) throws Exception {
    return mvc.perform(post("/offers/" + offerId + "/raise/" + ratio)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON));
  }

  private ResultActions place(String price) throws Exception {
    var json = "{" + "\"price\": \"" + price + "\"" + "}";
    return mvc.perform(post("/offers")
        .accept(MediaType.APPLICATION_JSON)
        .content(json)
        .contentType(MediaType.APPLICATION_JSON));
  }

}
