package awsm.domain.trading;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import awsm.util.concurrency.Threads;
import awsm.util.concurrency.Transactions;
import java.math.BigDecimal;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest
class OfferTest {

  @Autowired
  private Offers offers;

  @Autowired
  private PlatformTransactionManager txManager;

  @Test
  void throws_on_concurrent_modification() {
    var threads = new Threads(2);
    var tx = new Transactions(txManager);

    var offerId = tx.wrap(() -> {
      var anOffer = new Offer(new DecimalNumber("10.00"));
      offers.save(anOffer);
      return anOffer.id();
    }).get();

    threads.spinOff(tx.wrap(() -> {
      var offer = offers.findById(offerId).orElseThrow();
      offer.raiseBy(new DecimalNumber("10.00"));
      threads.sync();
    }));

    threads.spinOff(tx.wrap(() -> {
      var offer = offers.findById(offerId).orElseThrow();
      offer.accept();
      threads.sync();
    }));

    var e = assertThrows(CompletionException.class, threads::waitForAll);
    assertThat(e).hasCauseInstanceOf(ObjectOptimisticLockingFailureException.class);
  }

}




