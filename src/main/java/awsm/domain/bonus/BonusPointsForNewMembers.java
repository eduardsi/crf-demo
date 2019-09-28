package awsm.domain.bonus;

import static java.lang.String.format;

import awsm.domain.registration.NewMemberEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class BonusPointsForNewMembers {

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  void whenever(NewMemberEvent event) {

    var member = event.member();
    var email = member.email().toString();
    var qualifiesForBonus = email.matches("/vip.com/");
    if (qualifiesForBonus) {
      System.out.println(format("A member %s qualifies for bonus", member.name()));
    } else {
      System.out.println(format("No bonus for %s", member.name()));
    }

  }

}
