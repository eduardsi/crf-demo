package awsm.domain.banking.customer;

import com.google.common.base.Splitter;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.get;

public class Email {

  private final String email;

  public Email(String email) {
    var isNotBlank = email != null && !email.isBlank();
    checkArgument(isNotBlank, "Email %s must not be blank", email);

    var isValid = email.contains("@");
    checkArgument(isValid, "Email %s must contain @", email);
    this.email = email;
  }

  @Override
  public final String toString() {
    return email;
  }

  public String domain() {
    return get(Splitter.on('@').split(email), 1);
  }

  public interface Uniqueness {

    boolean guaranteed(Email email);

    @Component
    class AcrossCustomers implements Uniqueness {

      private final Customer.Repo customerRepo;

      private AcrossCustomers(DSLContext db) {
        this.customerRepo = new Customer.Repo(db);
      }

      @Override
      public boolean guaranteed(Email email) {
        return !customerRepo.contains(email + "");
      }

    }

  }

}
