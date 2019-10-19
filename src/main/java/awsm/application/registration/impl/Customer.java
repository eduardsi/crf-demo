package awsm.application.registration.impl;

import static java.util.Objects.requireNonNull;

import awsm.application.registration.Register;
import java.sql.ResultSet;
import java.sql.SQLException;

class Customer {

  private final Email email;

  private final FullName name;

  Customer(FullName name, Email email) {
    this.name = requireNonNull(name, "Name cannot be null");
    this.email = requireNonNull(email, "Email cannot be null");
    new Register.RegistrationCompleted(name + "", email + "").schedule();
  }

  Customer(ResultSet rs) throws SQLException {
    this.name = new FullName(rs.getString("first_name"), rs.getString("last_name"));
    this.email = new Email(rs.getString("email"));
  }

  public FullName name() {
    return name;
  }

  public Email email() {
    return email;
  }

}
