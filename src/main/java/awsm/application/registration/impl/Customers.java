package awsm.application.registration.impl;

import java.util.HashMap;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
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
    var args = new HashMap<String, Object>();
    args.put("email", customer.email() + "");
    args.put("first_name", customer.name().firstName);
    args.put("last_name", customer.name().lastName);

    var jdbcInsert = new SimpleJdbcInsert(dataSource);
    return (long) jdbcInsert
        .withTableName("customers")
        .usingGeneratedKeyColumns("id")
        .executeAndReturnKey(args);
  }

  Customer singleById(long id) {
    return jdbc.queryForObject("SELECT c.* FROM customers c WHERE c.id = ?", (rs, rowNo) -> new Customer(rs), id);
  }

  Optional<Customer> singleByEmail(String email) {
    var hits = jdbc.query("SELECT c.* FROM customers c WHERE c.email = ?", (rs, rowNo) -> new Customer(rs), email);
    return hits.stream().findFirst();
  }

}
