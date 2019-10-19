package awsm.application.registration.impl;

import static org.assertj.core.api.Assertions.assertThat;

import awsm.application.registration.Register;
import awsm.infrastructure.modeling.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("a customer")
class CustomerTest {

  private static final Email.Uniqueness uniqueness = email -> true;
  private static final Email.Blacklist blacklist = email -> true;

  @Test
  void schedules_an_event_upon_creation() {

    var customer = new Customer(
        new FullName("Uncle", "Bob"),
        new Email("uncle@domain.com", uniqueness, blacklist));

    var event = (Register.RegistrationCompleted) DomainEvent.lastPublished.get();
    assertThat(event.fullName).isEqualTo("Uncle Bob");
    assertThat(event.email).isEqualTo("uncle@domain.com");
  }


}
