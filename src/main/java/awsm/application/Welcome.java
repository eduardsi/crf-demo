package awsm.application;

import static awsm.infra.middleware.ReturnsNothing.NOTHING;

import awsm.domain.DomainEvent;
import awsm.domain.registration.Members;
import awsm.domain.registration.Registration;
import awsm.infra.middleware.Command;
import awsm.infra.middleware.ReturnsNothing;
import awsm.infra.middleware.impl.react.Reaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

class Welcome implements Command<ReturnsNothing> {

  private final long memberId;

  private Welcome(@JsonProperty("memberId") long memberId) {
    this.memberId = memberId;
  }

  @Component
  static class Scheduled implements DomainEvent.Listener<Registration> {
    @Override
    public void beforeCommit(Registration event) {
      var welcome = new Welcome(event.member().id());
      welcome.schedule();
    }
  }

  @Component
  static class Re implements Reaction<Welcome, ReturnsNothing> {

    private final Members members;

    private Re(Members members) {
      this.members = members;
    }

    @Override
    public ReturnsNothing react(Welcome command) {
      var member = members.findById(command.memberId).orElseThrow();
      System.out.println("Welcome to the Matrix, " + member.name());
      return NOTHING;
    }
  }
}