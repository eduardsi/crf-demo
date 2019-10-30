package awsm.crm;

import static jooq.tables.Customer.CUSTOMER;

import java.util.function.Function;
import jooq.tables.records.CustomerRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

class Customer {

  private final Email email;

  private final FullName name;

  private CustomerId id = new CustomerId();

  Customer(FullName name, RegistrationEmail email) {
    this.email = email;
    this.name = name;
  }

  private Customer(FullName name, Email email) {
    this.email = email;
    this.name = name;
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

  CustomerId id() {
    return id;
  }

  @Component
  static class Repository {

    private final DSLContext dsl;

    public Repository(DSLContext dsl) {
      this.dsl = dsl;
    }

    private void insert(Customer self) {
      var id = dsl
          .insertInto(CUSTOMER)
          .set(CUSTOMER.EMAIL, self.email + "")
          .set(CUSTOMER.FIRST_NAME, self.name.firstName)
          .set(CUSTOMER.LAST_NAME, self.name.lastName)
          .returning(CUSTOMER.ID)
          .fetchOne()
          .getId();
      self.id = new CustomerId(id);
    }

    Customer singleBy(CustomerId id) {
      return dsl
          .selectFrom(CUSTOMER)
          .where(CUSTOMER.ID.equal(id.asLong()))
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
        var fullName = new FullName(jooq.getFirstName(), jooq.getLastName());
        var email = new Email(jooq.getEmail());
        return new Customer(fullName, email);
      };
    }


  }
}
