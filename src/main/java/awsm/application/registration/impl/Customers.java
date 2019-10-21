package awsm.application.registration.impl;

import static jooq.tables.Customer.CUSTOMER;

import java.util.Optional;
import javax.sql.DataSource;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
class Customers {

  private final JdbcTemplate jdbc;
  private final DataSource dataSource;

  public Customers(JdbcTemplate jdbc, DataSource dataSource) {
    this.jdbc = jdbc;
    this.dataSource = dataSource;
  }

  long add(Customer customer) {
    return DSL.using(dataSource, SQLDialect.POSTGRES)
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
