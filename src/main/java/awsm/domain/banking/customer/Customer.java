package awsm.domain.banking.customer;

import static jooq.tables.Customer.CUSTOMER;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

import jooq.tables.records.CustomerRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

public class Customer {

  public enum Status {
    PENDING, CONFIRMED
  }

  private Status status = Status.PENDING;

  private final Email email;

  private final Name name;

  private String countryOfResidence;

  private LocalDate dateOfBirth;

  private Optional<Long> id = Optional.empty();

  public Customer(Name name, Email email, LocalDate dateOfBirth, String countryOfResidence) {
    this.email = email;
    this.name = name;
    this.dateOfBirth = dateOfBirth;
    this.countryOfResidence = countryOfResidence;
  }

  public Name name() {
    return name;
  }

  public Email email() {
    return email;
  }

  public String countryOfResidence() {
    return countryOfResidence;
  }

  public LocalDate dateOfBirth() {
    return dateOfBirth;
  }

  public void saveNew(Repository repo) {
    repo.insert(this);
  }

  public long id() {
    return id.orElseThrow();
  }

  public void confirm(Repository repo) {
    this.status = Status.CONFIRMED;
    repo.update(this);
  }

  @Component
  public static class Repository {

    private final DSLContext dsl;

    public Repository(DSLContext dsl) {
      this.dsl = dsl;
    }

    private void insert(Customer self) {
      var id = dsl
          .insertInto(CUSTOMER)
          .set(toJooq(self))
          .returning(CUSTOMER.ID)
          .fetchOne()
          .getId();
      self.id = Optional.of(id);
    }

    private void update(Customer self) {
      dsl.update(CUSTOMER)
              .set(toJooq(self))
              .where(CUSTOMER.ID.equal(self.id()))
              .execute();
    }

    private CustomerRecord toJooq(Customer self) {
      return new CustomerRecord()
              .setEmail(self.email + "")
              .setStatus(self.status.name())
              .setDateOfBirth(self.dateOfBirth)
              .setCountryOfResidence(self.countryOfResidence)
              .setFirstName(self.name.firstName)
              .setLastName(self.name.lastName);
    }

    public Customer findBy(long id) {
      return dsl
          .selectFrom(CUSTOMER)
          .where(CUSTOMER.ID.equal(id))
          .fetchOptional()
          .map(fromJooq())
          .orElseThrow();
    }

    boolean contains(String email) {
      return dsl
          .selectCount()
          .from(CUSTOMER)
          .where(CUSTOMER.EMAIL.equal(email))
          .fetchOne(0, int.class) > 0;
    }

    private Function<CustomerRecord, Customer> fromJooq() {
      return jooq -> {
        var fullName = new Name(jooq.getFirstName(), jooq.getLastName());
        var email = new Email(jooq.getEmail());
        var customer = new Customer(fullName, email, jooq.getDateOfBirth(), jooq.getCountryOfResidence());
        customer.status = Status.valueOf(jooq.getStatus());
        return customer;
      };
    }

  }
}
