package awsm.domain.loyalty;

import awsm.domain.banking.customer.Customer;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class Bonus {

  public void grant(Customer customer) {
    var qualifiesForBonus = customer.email().domain().matches("/vip.com/");
    if (qualifiesForBonus) {
      System.out.println(format("A customer %s qualifies for bonus", customer.name()));
    } else {
      System.out.println(format("No bonus for %s", customer.name()));
    }

  }
}
