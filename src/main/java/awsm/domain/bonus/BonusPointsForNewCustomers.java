package awsm.domain.bonus;

import static java.lang.String.format;

import awsm.domain.registration.CustomerRegistered;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class BonusPointsForNewCustomers {

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  void whenever(CustomerRegistered it) {

    var customer = it.customer();
    var email = customer.email().toString();
    var qualifiesForBonus = email.matches("/vip.com/");
    if (qualifiesForBonus) {
      System.out.println(format("A customer %s qualifies for bonus", customer.name()));
    } else {
      System.out.println(format("No bonus for %s", customer.name()));
    }

  }

}
