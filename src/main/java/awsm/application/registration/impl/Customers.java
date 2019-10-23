package awsm.application.registration.impl;

import static jooq.tables.Customer.CUSTOMER;

import java.util.Optional;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
class Customers {

  private final JdbcTemplate jdbc;
  private final DSLContext dsl;

  public Customers(JdbcTemplate jdbc, DSLContext dsl) {
    this.jdbc = jdbc;
    this.dsl = dsl;
  }

  long add(Customer customer) {
    return dsl
        .insertInto(CUSTOMER, CUSTOMER.EMAIL, CUSTOMER.FIRST_NAME, CUSTOMER.LAST_NAME)
        .values(customer.email() + "", customer.name().firstName, customer.name().lastName)
        .returning(CUSTOMER.ID)
        .fetchOne()
        .getId();
  }

  Customer singleById(long id) {
    return jdbc.queryForObject("SELECT c.* FROM customer c WHERE c.id = ?", (rs, rowNo) -> new Customer(rs), id);
  }

  Optional<Customer> singleByEmail(String email) {
    var hits = jdbc.query("SELECT c.* FROM customer c WHERE c.email = ?", (rs, rowNo) -> new Customer(rs), email);
    return hits.stream().findFirst();
  }

}
