package awsm.domain.registration;

import static org.assertj.core.api.Assertions.assertThat;

import awsm.domain.DomainEvent;
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

    var event = (CustomerRegistered) DomainEvent.lastPublished.get();
    assertThat(event.customer()).isEqualTo(customer);
  }


}
