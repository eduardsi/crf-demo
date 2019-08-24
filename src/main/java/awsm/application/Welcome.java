package awsm.application;

import static awsm.infra.middleware.ReturnsNothing.NOTHING;

import awsm.domain.registration.Members;
import awsm.infra.middleware.Command;
import awsm.infra.middleware.ReturnsNothing;
import awsm.infra.middleware.impl.react.Reaction;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

class Welcome implements Command<ReturnsNothing> {

  private final long memberId;

  Welcome(@JsonProperty("memberId") long memberId) {
    this.memberId = memberId;
  }

  @Component
  static class Re implements Reaction<Welcome, ReturnsNothing> {

    private final Members members;

    private Re(Members members) {
      this.members = members;
    }

    @Override
    public ReturnsNothing react(Welcome cmd) {
      var member = members.findById(cmd.memberId).orElseThrow();
      System.out.printf("Sending email to %s: Welcome to the Matrix, %s", member.email(), member.name());
      return NOTHING;
    }
  }
}
