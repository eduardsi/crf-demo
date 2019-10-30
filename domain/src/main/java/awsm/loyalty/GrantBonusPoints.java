package awsm.loyalty;

import static java.lang.String.format;

import awsm.crm.RegistrationCompleted;
import awsm.infrastructure.middleware.NotificationHandler;
import org.springframework.stereotype.Component;

@Component
class GrantBonusPoints implements NotificationHandler<RegistrationCompleted> {

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
