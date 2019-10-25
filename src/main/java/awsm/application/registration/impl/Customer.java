package awsm.application.registration.impl;

import static java.util.Objects.requireNonNull;
import static jooq.tables.Customer.CUSTOMER;

import awsm.application.registration.Register;
import java.util.Optional;
import java.util.function.Function;
import jooq.tables.records.CustomerRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

class Customer {

  private final Email email;

  private final FullName name;

  private Optional<Long> id = Optional.empty();

  Customer(FullName name, Email email) {
    this.name = requireNonNull(name, "Name cannot be null");
    this.email = requireNonNull(email, "Email cannot be null");
    new Register.RegistrationCompleted(name + "", email + "").schedule();
  }

  public FullName name() {
    return name;
  }

  public Email email() {
    return email;
  }

  void register(Repository repository) {
    repository.insert(this);
  }

  long id() {
    return id.orElseThrow();
  }

  @Component
  static class Repository {

    private final DSLContext dsl;

    public Repository(DSLContext dsl) {
      this.dsl = dsl;
    }

    void insert(Customer self) {
      var id = dsl
          .insertInto(CUSTOMER)
          .set(CUSTOMER.EMAIL, self.email + "")
          .set(CUSTOMER.FIRST_NAME, self.name.firstName)
          .set(CUSTOMER.LAST_NAME, self.name.lastName)
          .returning(CUSTOMER.ID)
          .fetchOne()
          .getId();
      self.id = Optional.of(id);
    }

    Customer singleBy(long id) {
      return dsl
          .selectFrom(CUSTOMER)
          .where(CUSTOMER.ID.equal(id))
          .fetchOptional()
          .map(fromJooq())
          .orElseThrow();
    }

    Optional<Customer> singleBy(String email) {
      return dsl
          .selectFrom(CUSTOMER)
          .where(CUSTOMER.EMAIL.equal(email))
          .fetchOptional()
          .map(fromJooq());
    }

    private Function<CustomerRecord, Customer> fromJooq() {
      return jooq -> {
        var fullName = new FullName(jooq.getFirstName(), jooq.getLastName());
        var email = new Email(jooq.getEmail());
        return new Customer(fullName, email);
      };
    }


  }
}
