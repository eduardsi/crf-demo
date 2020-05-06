package awsm.domain.banking.customer;

import static jooq.tables.Customer.CUSTOMER;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

import jooq.tables.records.CustomerRecord;
import org.jooq.DSLContext;

public class Customer {

  private Email email;

  private Name name;

  private String countryOfResidence;

  private LocalDate dateOfBirth;

  private Optional<Long> id = Optional.empty();

  public Customer(Name name, Email email, LocalDate dateOfBirth, String countryOfResidence) {
    this.email = email;
    this.name = name;
    this.dateOfBirth = dateOfBirth;
    this.countryOfResidence = countryOfResidence;
  }

  private Customer() {
  }

  public Name name() {
    return name;
  }

  public Email email() {
    return email;
  }

  public void saveNew(DSLContext db) {
    var id = db
        .insertInto(CUSTOMER)
            .set(CUSTOMER.EMAIL, this.email + "")
            .set(CUSTOMER.DATE_OF_BIRTH, this.dateOfBirth)
            .set(CUSTOMER.COUNTRY_OF_RESIDENCE, this.countryOfResidence)
            .set(CUSTOMER.FIRST_NAME, this.name.firstName)
            .set(CUSTOMER.LAST_NAME, this.name.lastName)
        .returning(CUSTOMER.ID)
        .fetchOne()
        .getId();
    this.id = Optional.of(id);
  }

  public long id() {
    return id.orElseThrow();
  }

  public static class Repo {

    private final DSLContext dsl;

    public Repo(DSLContext dsl) {
      this.dsl = dsl;
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
        var customer = new Customer();
        customer.name = new Name(jooq.getFirstName(), jooq.getLastName());
        customer.email = new Email(jooq.getEmail());
        customer.countryOfResidence = jooq.getCountryOfResidence();
        customer.dateOfBirth = jooq.getDateOfBirth();
        customer.id = Optional.of(jooq.getId());
        return customer;
      };
    }

  }
}
