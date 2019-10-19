package awsm.application.trading;

import static awsm.application.trading.impl.$.$;

import awsm.application.trading.impl.Offers;
import awsm.infrastructure.hashing.UnhashId;
import awsm.infrastructure.middleware.Command;
import awsm.infrastructure.middleware.impl.react.Reaction;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

class RaiseOfferPrice implements Command<BigDecimal> {

  private final String offerId;
  private final BigDecimal ratio;

  private RaiseOfferPrice(String offerId, BigDecimal ratio) {
    this.offerId = offerId;
    this.ratio = ratio;
  }


  @RestController
  static class Http {
    @PostMapping("/offers/{offerId}/raise/{ratio}")
    BigDecimal accept(@PathVariable String offerId, @PathVariable BigDecimal ratio)  {
      return new RaiseOfferPrice(offerId, ratio).execute();
    }
  }

  @Component
  static class Re implements Reaction<RaiseOfferPrice, BigDecimal> {

    private final Offers offers;

    Re(Offers offers) {
      this.offers = offers;
    }

    @Override
    public BigDecimal react(RaiseOfferPrice cmd) {
      var offerId = new UnhashId(cmd.offerId).asLong();
      var offer = offers.singleById(offerId).orElseThrow();
      var ratio = $(cmd.ratio);
      offer.raiseBy(ratio);
      return offer.price().big();
    }
  }

}
