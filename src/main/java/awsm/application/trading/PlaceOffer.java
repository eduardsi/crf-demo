package awsm.application.trading;

import static awsm.application.trading.impl.$.$;

import awsm.application.trading.impl.Offer;
import awsm.application.trading.impl.Offers;
import awsm.infrastructure.hashing.HashId;
import awsm.infrastructure.middleware.MiddlewareCommand;
import awsm.infrastructure.middleware.impl.react.Reaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

class PlaceOffer implements MiddlewareCommand<CharSequence> {

  private final BigDecimal price;

  PlaceOffer(@JsonProperty("price") BigDecimal price) {
    this.price = price;
  }

  @RestController
  static class Http {
    @PostMapping("/offers")
    CharSequence accept(@RequestBody PlaceOffer placeOffer)  {
      return placeOffer.execute();
    }
  }

  @Component
  static class Re implements Reaction<PlaceOffer, CharSequence> {

    private final Offers offers;

    Re(Offers offers) {
      this.offers = offers;
    }

    @Override
    public CharSequence react(PlaceOffer cmd) {
      var price = $(cmd.price);
      var offer = new Offer(price);
      return new HashId(offers.add(offer));
    }
  }

}
