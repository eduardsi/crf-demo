//package awsm.application.trading;
//
//import static awsm.application.trading.impl.$.$;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import awsm.application.trading.impl.Offer;
//import awsm.application.trading.impl.Offers;
//import awsm.util.tx.Transactions;
//import java.util.concurrent.CompletionException;
//import java.util.concurrent.CountDownLatch;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.dao.CannotAcquireLockException;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@SpringBootTest
//@DisplayName("an offer")
//class OfferTest {
//
//  @Autowired
//  private Offers offers;
//
//  @Autowired
//  private PlatformTransactionManager txManager;
//
//  @Test
//  void throws_on_concurrent_modification() throws InterruptedException {
//    var countdown = new CountDownLatch(1);
//    var tx = new Transactions(txManager);
//
//    var offerId = tx.wrap(() -> {
//      var anOffer = new Offer($("10.00"));
//      return offers.add(anOffer);
//    }).get();
//
//    new Thread(tx.wrap(() -> {
//      var offer = offers.singleById(offerId).orElseThrow();
//      offer.raiseBy($("1.00"));
//      offers.save(offerId, offer);
//      countdown.countDown();
//    })).start();
//
//
//    var e = assertThrows(CompletionException.class, () -> tx.wrap(() -> {
//      var offer = offers.singleById(offerId).orElseThrow();
//      offer.accept();
//      offers.save(offerId, offer);
//    }));
//    assertThat(e).hasCauseInstanceOf(CannotAcquireLockException.class);
//  }
//
//}