package awsm.application.loyalty;

import static java.lang.String.format;

import awsm.application.registration.Register;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class BonusPointsForNewCustomers {

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  void whenever(Register.RegistrationCompleted it) {

    var qualifiesForBonus = it.email.matches("/vip.com/");
    if (qualifiesForBonus) {
      System.out.println(format("A customer %s qualifies for bonus", it.fullName));
    } else {
      System.out.println(format("No bonus for %s", it.fullName));
    }

  }

}
