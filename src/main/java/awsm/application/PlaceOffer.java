package awsm.application;

import awsm.domain.offers.DecimalNumber;
import awsm.domain.offers.Offer;
import awsm.domain.offers.Offers;
import awsm.infra.hashing.HashId;
import awsm.infra.middleware.Command;
import awsm.infra.middleware.impl.react.Reaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

class PlaceOffer implements Command<CharSequence> {

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
      var price = new DecimalNumber(cmd.price);
      var offer = new Offer(price);
      offers.save(offer);
      return new HashId(offer.id());
    }
  }

}
