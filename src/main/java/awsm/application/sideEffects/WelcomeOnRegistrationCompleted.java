package awsm.application.sideEffects;

import awsm.domain.registration.Members;
import awsm.domain.registration.RegistrationCompleted;
import awsm.infra.modeling.SideEffect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class WelcomeOnRegistrationCompleted implements SideEffect<RegistrationCompleted> {

  private static final Logger logger = LoggerFactory.getLogger(WelcomeOnRegistrationCompleted.class);

  private final Members members;

  WelcomeOnRegistrationCompleted(Members members) {
    this.members = members;
  }

  @Override
  public void afterCommit(RegistrationCompleted event) {
    var member = members.findById(event.memberId()).orElseThrow();
    logger.info("After commit, sending greetings to {} to {}", member.name(), member.email());
  }

  @Override
  public void beforeCommit(RegistrationCompleted event) {
    var member = members.findById(event.memberId()).orElseThrow();
    logger.info("Before commit, sending greetings to {} to {}", member.name(), member.email());
  }


}
