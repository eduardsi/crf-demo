package awsm.application;

import static awsm.infra.middleware.ReturnsNothing.NOTHING;
import static com.google.common.base.Preconditions.checkState;

import awsm.domain.offers.DecimalNumber;
import awsm.domain.offers.Offers;
import awsm.infra.hashing.UnhashId;
import awsm.infra.middleware.Command;
import awsm.infra.middleware.ReturnsNothing;
import awsm.infra.middleware.impl.react.Reaction;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

class AcceptOffer implements Command<ReturnsNothing> {

  private final String offerId;
  private final BigDecimal limit;

  private AcceptOffer(String offerId, BigDecimal limit) {
    this.offerId = offerId;
    this.limit = limit;
  }

  @RestController
  static class HttpEntryPoint {
    @PostMapping("/offers/{offerId}/accept/limit/{limit}")
    ReturnsNothing accept(@PathVariable String offerId, @PathVariable BigDecimal limit) {
      return new AcceptOffer(offerId, limit).execute();
    }
  }

  @Component
  static class Re implements Reaction<AcceptOffer, ReturnsNothing> {

    private final Offers offers;

    Re(Offers offers) {
      this.offers = offers;
    }

    @Override
    public ReturnsNothing react(AcceptOffer cmd) {
      var offerId = new UnhashId(cmd.offerId).asLong();
      var offer = offers.findById(offerId).orElseThrow();

      var limit = new DecimalNumber(cmd.limit);
      var withinALimit = limit.isEqualOrGreaterThan(offer.price());

      checkState(withinALimit, "Offer price is not within a limit (%s/%s)", offer.price(), cmd.limit);

      offer.accept();

      return NOTHING;
    }
  }
}
