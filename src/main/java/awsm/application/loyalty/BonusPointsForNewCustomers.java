package awsm.application.loyalty;

import static java.lang.String.format;

import awsm.application.registration.domain.RegistrationCompleted;
import awsm.infrastructure.middleware.SideEffect;
import org.springframework.stereotype.Component;

@Component
class BonusPointsForNewCustomers implements SideEffect<RegistrationCompleted> {

  @Override
  public void beforeCommit(RegistrationCompleted event) {
    var qualifiesForBonus = event.email.matches("/vip.com/");
    if (qualifiesForBonus) {
      System.out.println(format("A customer %s qualifies for bonus", event.fullName));
    } else {
      System.out.println(format("No bonus for %s", event.fullName));
    }

  }
}
